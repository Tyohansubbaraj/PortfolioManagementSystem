package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.config.ApiKeyConfig;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.dto.ChartPoint;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.repository.HoldingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Financial Advisor Service
 * Uses Google Gemini AI to provide investment recommendations
 */
@Service
public class FinancialAdvisorService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private PriceService priceService;

    /**
     * Get AI investment recommendation for a stock symbol
     * Fetches stock details from database and sends to Gemini AI for analysis
     *
     * @param symbol The stock symbol (e.g., "AAPL")
     * @return AI generated investment recommendation (BUY, HOLD, or SELL)
     */
    public String getInvestmentRecommendation(String symbol) {
        // Step 1: Fetch asset details from database
        Asset asset = assetRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Asset with symbol " + symbol + " not found"));

        System.out.println("========== FINANCIAL ADVISOR SERVICE ==========");
        System.out.println("Fetched Asset Details:");
        System.out.println("  Symbol: " + asset.getSymbol());
        System.out.println("  Name: " + asset.getName());
        System.out.println("  Type: " + asset.getType());
        System.out.println("  Sector: " + asset.getSector());
        System.out.println("  Current Price: $" + asset.getCurrentPrice());
        System.out.println("  Last Updated: " + asset.getLastUpdated());

        // Step 2: Fetch holding details if available
        Optional<Holding> holding = holdingRepository.findByAssetId(asset.getId());
        String holdingDetails = "";

        if (holding.isPresent()) {
            Holding h = holding.get();
            BigDecimal avgBuyPrice = h.getAvgBuyPrice();
            BigDecimal totalQuantity = h.getTotalQuantity();
            BigDecimal currentPrice = asset.getCurrentPrice();

            BigDecimal gainLoss = currentPrice.subtract(avgBuyPrice);
            BigDecimal gainLossPercent = gainLoss.divide(avgBuyPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            holdingDetails = String.format(
                    "\nCurrent Holdings:\n" +
                    "  Quantity: %s\n" +
                    "  Average Buy Price: $%s\n" +
                    "  Current Price: $%s\n" +
                    "  Gain/Loss: $%s (%.2f%%)",
                    totalQuantity, avgBuyPrice, currentPrice, gainLoss, gainLossPercent
            );

            System.out.println(holdingDetails);
        } else {
            holdingDetails = "\nNo current holdings for this asset.";
            System.out.println(holdingDetails);
        }

        // Step 3: Fetch price history and chart data
        String priceHistorySummary = fetchPriceHistoryData(symbol, holding.orElse(null));
        System.out.println(priceHistorySummary);

        // Step 4: Construct prompt for Gemini AI with all data
        String prompt = buildFinancialAdvisorPrompt(asset, holding.orElse(null), priceHistorySummary);

        System.out.println("\n========== SENDING TO GEMINI AI ==========");
        System.out.println("Prompt: " + prompt);

        // Step 5: Call Gemini API
        String recommendation = callGeminiForRecommendation(prompt);

        System.out.println("\n========== AI RECOMMENDATION ==========");
        System.out.println(recommendation);
        System.out.println("================================================\n");

        return recommendation;
    }

    /**
     * Fetch price history data and chart data for the asset
     */
    private String fetchPriceHistoryData(String symbol, Holding holding) {
        try {
            StringBuilder priceData = new StringBuilder();
            priceData.append("\nPrice History Data:");

            if (holding != null && holding.getBuyDate() != null) {
                String startDate = holding.getBuyDate().toString();
                List<ChartPoint> chartData = priceService.fetchChartData(symbol, startDate);

                if (chartData != null && !chartData.isEmpty()) {
                    priceData.append("\n  Chart Data Points: ").append(chartData.size());

                    // Get first and last prices for trend analysis
                    ChartPoint firstPoint = chartData.get(0);
                    ChartPoint lastPoint = chartData.get(chartData.size() - 1);

                    BigDecimal firstPrice = new BigDecimal(firstPoint.getPrice().toString());
                    BigDecimal lastPrice = new BigDecimal(lastPoint.getPrice().toString());
                    BigDecimal priceChange = lastPrice.subtract(firstPrice);
                    BigDecimal changePercent = firstPrice.compareTo(BigDecimal.ZERO) > 0
                        ? priceChange.divide(firstPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                        : BigDecimal.ZERO;

                    // Calculate high and low from chart data
                    BigDecimal highPrice = chartData.stream()
                            .map(p -> new BigDecimal(p.getPrice().toString()))
                            .max(BigDecimal::compareTo)
                            .orElse(lastPrice);

                    BigDecimal lowPrice = chartData.stream()
                            .map(p -> new BigDecimal(p.getPrice().toString()))
                            .min(BigDecimal::compareTo)
                            .orElse(lastPrice);

                    priceData.append("\n  Price Trend (from ").append(firstPoint.getDate()).append(" to ").append(lastPoint.getDate()).append("):");
                    priceData.append("\n    Starting Price: $").append(String.format("%.2f", firstPrice));
                    priceData.append("\n    Current Price: $").append(String.format("%.2f", lastPrice));
                    priceData.append("\n    Price Change: $").append(String.format("%.2f", priceChange)).append(" (").append(String.format("%.2f", changePercent)).append("%)");
                    priceData.append("\n    52-Week High (from data): $").append(String.format("%.2f", highPrice));
                    priceData.append("\n    52-Week Low (from data): $").append(String.format("%.2f", lowPrice));
                    priceData.append("\n    Volatility: ").append(calculateVolatility(chartData) ? "High" : "Moderate");
                } else {
                    priceData.append("\n  No historical chart data available");
                }
            } else {
                priceData.append("\n  No purchase date data - unable to fetch historical chart data");
            }

            return priceData.toString();
        } catch (Exception e) {
            System.err.println("Warning: Failed to fetch price history data: " + e.getMessage());
            return "\nPrice History Data: Unable to fetch (service unavailable)";
        }
    }

    /**
     * Calculate volatility from chart data
     */
    private boolean calculateVolatility(List<ChartPoint> chartData) {
        if (chartData == null || chartData.size() < 2) return false;

        List<BigDecimal> prices = chartData.stream()
                .map(p -> new BigDecimal(p.getPrice().toString()))
                .toList();

        BigDecimal mean = prices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.size()), 4, RoundingMode.HALF_UP);

        BigDecimal variance = prices.stream()
                .map(p -> p.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.size()), 4, RoundingMode.HALF_UP);

        // If variance is high, volatility is high
        return variance.compareTo(BigDecimal.valueOf(10)) > 0;
    }

    /**
     * Build a detailed prompt for the financial advisor AI
     */
    private String buildFinancialAdvisorPrompt(Asset asset, Holding holding, String priceHistorySummary) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a financial advisor. Based on the following comprehensive stock information, ");
        prompt.append("provide a detailed investment recommendation (BUY, HOLD, or SELL).\n\n");

        prompt.append("Stock Information:\n");
        prompt.append("- Symbol: ").append(asset.getSymbol()).append("\n");
        prompt.append("- Name: ").append(asset.getName()).append("\n");
        prompt.append("- Type: ").append(asset.getType()).append("\n");
        prompt.append("- Sector: ").append(asset.getSector()).append("\n");
        prompt.append("- Current Price: $").append(asset.getCurrentPrice()).append("\n");

        if (holding != null) {
            BigDecimal avgBuyPrice = holding.getAvgBuyPrice();
            BigDecimal gainLoss = asset.getCurrentPrice().subtract(avgBuyPrice);
            BigDecimal gainLossPercent = gainLoss.divide(avgBuyPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            prompt.append("\nYour Current Position:\n");
            prompt.append("- Quantity: ").append(holding.getTotalQuantity()).append("\n");
            prompt.append("- Average Buy Price: $").append(avgBuyPrice).append("\n");
            prompt.append("- Current Gain/Loss: ").append(gainLoss).append(" (").append(gainLossPercent).append("%)\n");
        } else {
            prompt.append("\nNo current position in this stock.\n");
        }

        prompt.append(priceHistorySummary);

        prompt.append("\n\nBased on the current market price, historical price trends, and your position (if any), ");
        prompt.append("should you BUY MORE, HOLD, or SELL? Provide a comprehensive bullet points recommendation with detailed reasoning in around 20 words / point.");

        return prompt.toString();
    }

    /**
     * Call Gemini API with the financial advisor prompt
     */
    private String callGeminiForRecommendation(String prompt) {
        try {
            // Set the API key
            String apiKey = ApiKeyConfig.getGeminiApiKey();
            System.setProperty("GEMINI_API_KEY", apiKey);

            // Initialize Gemini client
            Client client = new Client();

            // Call Gemini model
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    prompt,
                    null
            );

            return response.text();

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "Error getting recommendation from AI. Please check API configuration.";
        }
    }
}
