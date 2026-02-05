package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.dto.RecommendationResponse;
import com.marketminds.portfoliomanagementsystem.service.FinancialAdvisorService;
import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Financial Advisor AI
 * Exposes endpoints to get AI-powered investment recommendations
 */
@RestController
@RequestMapping("/api/advisor")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinancialAdvisorController {

    @Autowired
    private FinancialAdvisorService advisorService;

    @Autowired
    private AssetRepository assetRepository;

    /**
     * Get AI investment recommendation for a stock symbol
     * @param symbol Stock symbol (e.g., "AAPL")
     * @return RecommendationResponse with AI recommendation
     */
    @GetMapping("/recommendation/{symbol}")
    public ResponseEntity<RecommendationResponse> getRecommendation(@PathVariable String symbol) {
        try {
            // Get stock details from database
            Asset asset = assetRepository.findBySymbol(symbol)
                    .orElse(null);

            if (asset == null) {
                RecommendationResponse errorResponse = new RecommendationResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Stock symbol not found: " + symbol);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get AI recommendation
            String aiResponse = advisorService.getInvestmentRecommendation(symbol);

            // Package the response
            RecommendationResponse response = new RecommendationResponse();
            response.setSymbol(asset.getSymbol());
            response.setName(asset.getName());
            response.setSector(asset.getSector());
            response.setType(asset.getType());
            response.setCurrentPrice(asset.getCurrentPrice().toString());
            response.setRecommendation(aiResponse);
            response.setStatus("success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            RecommendationResponse errorResponse = new RecommendationResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
