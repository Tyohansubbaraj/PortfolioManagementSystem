package com.marketminds.portfoliomanagementsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssetDto {

    private Long id;
    private String symbol;
    private String name;
    private String type;
    private String sector;
    private BigDecimal currentPrice;
    private LocalDateTime lastUpdated;
    // Added fields expected by frontend
    private BigDecimal price;
    private BigDecimal quantity;

    public AssetDto(Long id, String symbol, String name, String type, String sector, BigDecimal currentPrice, LocalDateTime lastUpdated) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.sector = sector;
        this.currentPrice = currentPrice;
        this.lastUpdated = lastUpdated;
        this.price = currentPrice;
        this.quantity = BigDecimal.ZERO;
    }

    // Convenience constructor that includes price and quantity for the frontend
    public AssetDto(Long id, String symbol, String name, String type, String sector, BigDecimal price, BigDecimal quantity, LocalDateTime lastUpdated) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.sector = sector;
        this.currentPrice = price;
        this.price = price;
        this.quantity = quantity == null ? BigDecimal.ZERO : quantity;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSector() {
        return sector;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    // New getters used by the frontend
    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
