package com.marketminds.portfoliomanagementsystem.dto;

/**
 * DTO for AI Recommendation Response
 * Packages the AI recommendation to send to frontend
 */
public class RecommendationResponse {
    private String symbol;
    private String name;
    private String sector;
    private String type;
    private String currentPrice;
    private String recommendation;
    private String status;
    private String message;

    // Constructors
    public RecommendationResponse() {
    }

    public RecommendationResponse(String symbol, String name, String sector, String type,
                                  String currentPrice, String recommendation, String status) {
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.type = type;
        this.currentPrice = currentPrice;
        this.recommendation = recommendation;
        this.status = status;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
