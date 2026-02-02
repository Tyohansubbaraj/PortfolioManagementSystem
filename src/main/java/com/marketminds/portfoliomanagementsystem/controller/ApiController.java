package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.dto.AssetDto;
import com.marketminds.portfoliomanagementsystem.dto.TransactionDto;
import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.repository.HoldingRepository;
import com.marketminds.portfoliomanagementsystem.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;

    public ApiController(AssetRepository assetRepository, TransactionRepository transactionRepository, HoldingRepository holdingRepository) {
        this.assetRepository = assetRepository;
        this.transactionRepository = transactionRepository;
        this.holdingRepository = holdingRepository;
    }

    @GetMapping("/assets")
    public ResponseEntity<List<AssetDto>> getAssets() {
        List<Asset> assets = assetRepository.findAll();
        // load holdings once to map quantities by asset id
        Map<Long, BigDecimal> qtyByAsset = holdingRepository.findAll().stream()
                .filter(h -> h.getAsset() != null && h.getTotalQuantity() != null)
                .collect(Collectors.toMap(h -> h.getAsset().getId(), Holding::getTotalQuantity));

        List<AssetDto> dtos = assets.stream()
                .map(a -> new AssetDto(
                        a.getId(),
                        a.getSymbol(),
                        a.getName(),
                        a.getType(),
                        a.getSector(),
                        a.getCurrentPrice(),
                        qtyByAsset.getOrDefault(a.getId(), BigDecimal.ZERO),
                        a.getLastUpdated()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam(name = "assetId", required = false) Long assetId,
                                             @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                             @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                             @RequestParam(name = "page", required = false) Integer page,
                                             @RequestParam(name = "size", required = false) Integer size) {

        // If page param is provided, return a paged response
        if (page != null) {
            int p = Math.max(0, page);
            int s = (size == null || size <= 0) ? 50 : size;
            Pageable pageable = PageRequest.of(p, s);

            Page<Transaction> resultPage;
            if (assetId != null && startDate != null && endDate != null) {
                resultPage = transactionRepository.findTransactionsByAssetAndDateRange(assetId, startDate, endDate, pageable);
            } else if (startDate != null && endDate != null) {
                resultPage = transactionRepository.findTransactionsByDateRange(startDate, endDate, pageable);
            } else if (assetId != null) {
                resultPage = transactionRepository.findByAssetId(assetId, pageable);
            } else {
                resultPage = transactionRepository.findAll(pageable);
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("content", resultPage.getContent().stream().map(t -> toDto(t)).collect(Collectors.toList()));
            payload.put("page", resultPage.getNumber());
            payload.put("size", resultPage.getSize());
            payload.put("totalElements", resultPage.getTotalElements());
            payload.put("totalPages", resultPage.getTotalPages());
            return ResponseEntity.ok(payload);
        }

        // Fallback: return full list for backward compatibility
        List<Transaction> txs;
        if (assetId != null && startDate != null && endDate != null) {
            txs = transactionRepository.findTransactionsByAssetAndDateRange(assetId, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            txs = transactionRepository.findTransactionsByDateRange(startDate, endDate);
        } else if (assetId != null) {
            txs = transactionRepository.findByAssetIdOrderByTradeDateDesc(assetId);
        } else {
            txs = transactionRepository.findAll();
            txs.sort(Comparator.comparing(Transaction::getTradeDate).reversed());
        }

        List<TransactionDto> dtos = txs.stream().map(t -> toDto(t)).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private TransactionDto toDto(Transaction t) {
        Long aid = null;
        String symbol = null;
        Asset asset = t.getAsset();
        if (asset != null) {
            aid = asset.getId();
            symbol = asset.getSymbol();
        }
        return new TransactionDto(t.getId(), aid, symbol, t.getType(), t.getQuantity(), t.getPrice(), t.getTradeDate());
    }

    /**
     * Simple portfolio history: returns daily snapshots for the last `days` days using current asset prices and holdings table.
     * This is an approximation and uses current prices for all days. For accurate historical valuation you need price history.
     */
    @GetMapping("/portfolio/history")
    public ResponseEntity<List<Map<String, Object>>> getPortfolioHistory(@RequestParam(name = "days", required = false, defaultValue = "30") int days) {
        if (days <= 0) days = 30;
        List<Holding> holdings = holdingRepository.findAll();

        List<Map<String, Object>> history = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            BigDecimal total = BigDecimal.ZERO;
            for (Holding h : holdings) {
                Asset a = h.getAsset();
                BigDecimal qty = h.getTotalQuantity() == null ? BigDecimal.ZERO : h.getTotalQuantity();
                BigDecimal price = a == null || a.getCurrentPrice() == null ? BigDecimal.ZERO : a.getCurrentPrice();
                total = total.add(price.multiply(qty));
            }
            Map<String, Object> point = new HashMap<>();
            point.put("date", d.toString());
            point.put("value", total);
            history.add(point);
        }

        return ResponseEntity.ok(history);
    }

    @GetMapping("/analytics/allocation")
    public ResponseEntity<List<Map<String, Object>>> getAllocation() {
        // Allocation by asset type (Stock/Bond/etc) based on holdings * currentPrice
        List<Asset> assets = assetRepository.findAll();
        Map<Long, BigDecimal> qtyByAsset = holdingRepository.findAll().stream()
                .filter(h -> h.getAsset() != null && h.getTotalQuantity() != null)
                .collect(Collectors.toMap(h -> h.getAsset().getId(), Holding::getTotalQuantity));

        Map<String, BigDecimal> byType = new HashMap<>();
        for (Asset a : assets) {
            BigDecimal qty = qtyByAsset.getOrDefault(a.getId(), BigDecimal.ZERO);
            BigDecimal price = a.getCurrentPrice() == null ? BigDecimal.ZERO : a.getCurrentPrice();
            BigDecimal val = price.multiply(qty);
            String type = a.getType() == null ? "Other" : a.getType();
            byType.put(type, byType.getOrDefault(type, BigDecimal.ZERO).add(val));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : byType.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("category", e.getKey());
            m.put("amount", e.getValue());
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/pl-heatmap")
    public ResponseEntity<List<Map<String, Object>>> getPLHeatmap() {
        // Compute simple P/L percent per asset using Holding.avgBuyPrice vs Asset.currentPrice
        List<Holding> holdings = holdingRepository.findAll();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Holding h : holdings) {
            Asset a = h.getAsset();
            if (a == null) continue;
            BigDecimal qty = h.getTotalQuantity() == null ? BigDecimal.ZERO : h.getTotalQuantity();
            BigDecimal current = a.getCurrentPrice() == null ? BigDecimal.ZERO : a.getCurrentPrice();
            BigDecimal avgBuy = h.getAvgBuyPrice() == null ? BigDecimal.ZERO : h.getAvgBuyPrice();
            BigDecimal pnlPercent = BigDecimal.ZERO;
            if (avgBuy.compareTo(BigDecimal.ZERO) != 0) {
                pnlPercent = current.subtract(avgBuy).divide(avgBuy, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            }
            BigDecimal value = current.multiply(qty);
            Map<String, Object> m = new HashMap<>();
            m.put("symbol", a.getSymbol());
            m.put("pnlPercent", pnlPercent);
            m.put("value", value);
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        List<Holding> holdings = holdingRepository.findAll();
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        List<Map<String, Object>> movers = new ArrayList<>();

        for (Holding h : holdings) {
            Asset a = h.getAsset();
            if (a == null) continue;
            BigDecimal qty = h.getTotalQuantity() == null ? BigDecimal.ZERO : h.getTotalQuantity();
            BigDecimal current = a.getCurrentPrice() == null ? BigDecimal.ZERO : a.getCurrentPrice();
            BigDecimal avgBuy = h.getAvgBuyPrice() == null ? BigDecimal.ZERO : h.getAvgBuyPrice();
            BigDecimal value = current.multiply(qty);
            totalValue = totalValue.add(value);
            BigDecimal cost = avgBuy.multiply(qty);
            totalCost = totalCost.add(cost);
            BigDecimal pnlPct = BigDecimal.ZERO;
            if (avgBuy.compareTo(BigDecimal.ZERO) != 0) {
                pnlPct = current.subtract(avgBuy).divide(avgBuy, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            }
            Map<String, Object> m = new HashMap<>();
            m.put("symbol", a.getSymbol());
            m.put("pnlPercent", pnlPct);
            m.put("value", value);
            movers.add(m);
        }

        BigDecimal totalUnrealized = totalValue.subtract(totalCost);
        // sort movers by pnl percent desc
        movers.sort((m1,m2) -> {
            BigDecimal p1 = new BigDecimal(m1.get("pnlPercent").toString());
            BigDecimal p2 = new BigDecimal(m2.get("pnlPercent").toString());
            return p2.compareTo(p1);
        });

        Map<String, Object> payload = new HashMap<>();
        payload.put("totalValue", totalValue);
        payload.put("totalUnrealizedPL", totalUnrealized);
        payload.put("holdingsCount", holdings.size());
        payload.put("topMovers", movers.stream().limit(5).collect(Collectors.toList()));
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/analytics/rebalance")
    public ResponseEntity<Map<String, Object>> getRebalanceSuggestions() {
        // Default target allocation by type
        Map<String, BigDecimal> target = new HashMap<>();
        target.put("Stock", BigDecimal.valueOf(0.60));
        target.put("Bond", BigDecimal.valueOf(0.30));
        target.put("Cash", BigDecimal.valueOf(0.10));

        // compute current allocation by type
        List<Asset> assets = assetRepository.findAll();
        Map<Long, BigDecimal> qtyByAsset = holdingRepository.findAll().stream()
                .filter(h -> h.getAsset() != null && h.getTotalQuantity() != null)
                .collect(Collectors.toMap(h -> h.getAsset().getId(), Holding::getTotalQuantity));

        Map<String, BigDecimal> byTypeValue = new HashMap<>();
        Map<String, List<Map<String, Object>>> assetsByType = new HashMap<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Asset a : assets) {
            BigDecimal qty = qtyByAsset.getOrDefault(a.getId(), BigDecimal.ZERO);
            BigDecimal price = a.getCurrentPrice() == null ? BigDecimal.ZERO : a.getCurrentPrice();
            BigDecimal val = price.multiply(qty);
            totalValue = totalValue.add(val);
            String type = a.getType() == null ? "Other" : a.getType();
            byTypeValue.put(type, byTypeValue.getOrDefault(type, BigDecimal.ZERO).add(val));
            Map<String, Object> assetMap = new HashMap<>();
            assetMap.put("symbol", a.getSymbol());
            assetMap.put("value", val);
            assetMap.put("price", price);
            assetMap.put("quantity", qty);
            assetsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(assetMap);
        }

        // compute suggestions per type (amount in currency to buy/sell)
        Map<String, BigDecimal> suggestions = new HashMap<>();
        for (Map.Entry<String, BigDecimal> e : byTypeValue.entrySet()) {
            String type = e.getKey();
            BigDecimal currentVal = e.getValue();
            BigDecimal tgtPct = target.getOrDefault(type, BigDecimal.ZERO);
            BigDecimal desired = totalValue.multiply(tgtPct);
            BigDecimal delta = desired.subtract(currentVal); // positive => buy, negative => sell
            suggestions.put(type, delta);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("totalValue", totalValue);
        payload.put("currentAllocation", byTypeValue);
        payload.put("targetAllocation", target);
        payload.put("suggestionsByType", suggestions);
        payload.put("assetsByType", assetsByType);
        return ResponseEntity.ok(payload);
    }

    // --- AI / utility endpoints (stubs / simple implementations) ---

    @GetMapping("/ai/forecast")
    public ResponseEntity<List<Map<String, Object>>> forecastPrices(@RequestParam(name = "symbol") String symbol,
                                                                     @RequestParam(name = "days", required = false, defaultValue = "7") int days) {
        // Simple stub: use current price and add small drift to create naive forecast
        Optional<Asset> maybe = assetRepository.findBySymbol(symbol);
        BigDecimal base = maybe.map(Asset::getCurrentPrice).orElse(BigDecimal.valueOf(100));
        List<Map<String, Object>> out = new ArrayList<>();
        Random rnd = new Random(symbol.hashCode() ^ LocalDate.now().getDayOfYear());
        BigDecimal last = base;
        for (int i = 1; i <= days; i++) {
            LocalDate d = LocalDate.now().plusDays(i);
            // small drift +/- 2%
            double drift = (rnd.nextDouble() - 0.5) * 0.04;
            last = last.multiply(BigDecimal.valueOf(1.0 + drift)).setScale(4, RoundingMode.HALF_UP);
            out.add(Map.of("date", d.toString(), "predictedPrice", last));
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/ai/sentiment")
    public ResponseEntity<Map<String, Object>> sentimentFor(@RequestParam(name = "symbol") String symbol) {
        // Stub: return randomized sentiment score and simple summary
        Random rnd = new Random(symbol.hashCode());
        double score = (rnd.nextDouble() - 0.5) * 2.0; // -1 .. +1
        String label = score > 0.2 ? "Bullish" : score < -0.2 ? "Bearish" : "Neutral";
        Map<String, Object> res = new HashMap<>();
        res.put("symbol", symbol);
        res.put("score", BigDecimal.valueOf(score).setScale(3, RoundingMode.HALF_UP));
        res.put("label", label);
        res.put("summary", "This is a simulated sentiment score (stub). Integrate an NLP pipeline for real results.");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/export/portfolio")
    public ResponseEntity<String> exportPortfolioCsv(@RequestParam(name = "format", required = false, defaultValue = "csv") String format) {
        // Only CSV supported for now
        List<Holding> holdings = holdingRepository.findAll();
        StringWriter sw = new StringWriter();
        sw.append("symbol,quantity,avgBuyPrice,currentPrice,value\n");
        for (Holding h : holdings) {
            Asset a = h.getAsset();
            if (a == null) continue;
            BigDecimal qty = h.getTotalQuantity() == null ? BigDecimal.ZERO : h.getTotalQuantity();
            BigDecimal avg = h.getAvgBuyPrice() == null ? BigDecimal.ZERO : h.getAvgBuyPrice();
            BigDecimal cur = a.getCurrentPrice() == null ? BigDecimal.ZERO : a.getCurrentPrice();
            BigDecimal val = cur.multiply(qty);
            sw.append(String.format("%s,%s,%s,%s,%s\n", a.getSymbol(), qty.toPlainString(), avg.toPlainString(), cur.toPlainString(), val.toPlainString()));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Content-Disposition", "attachment; filename=portfolio.csv");
        return ResponseEntity.ok().headers(headers).body(sw.toString());
    }

    @PostMapping("/paper/trade")
    public ResponseEntity<Map<String, Object>> paperTrade(@RequestParam(name = "symbol") String symbol,
                                                          @RequestParam(name = "type") String type,
                                                          @RequestParam(name = "quantity") BigDecimal quantity) {
        // Very small stub: record not persisted — returns simulated execution
        Map<String, Object> res = new HashMap<>();
        Optional<Asset> maybe = assetRepository.findBySymbol(symbol);
        BigDecimal price = maybe.map(Asset::getCurrentPrice).orElse(BigDecimal.valueOf(100));
        res.put("symbol", symbol);
        res.put("type", type);
        res.put("quantity", quantity);
        res.put("executedPrice", price);
        res.put("status", "simulated");
        res.put("message", "Paper trade executed in simulated environment.");
        return ResponseEntity.ok(res);
    }

}
