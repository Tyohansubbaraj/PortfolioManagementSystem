package com.marketminds.portfoliomanagementsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    private Long id;
    private Long assetId;
    private String assetSymbol;
    private String type;
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDateTime tradeDate;

    public TransactionDto(Long id, Long assetId, String assetSymbol, String type, BigDecimal quantity, BigDecimal price, LocalDateTime tradeDate) {
        this.id = id;
        this.assetId = assetId;
        this.assetSymbol = assetSymbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.tradeDate = tradeDate;
    }

    public Long getId() {
        return id;
    }

    public Long getAssetId() {
        return assetId;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getTradeDate() {
        return tradeDate;
    }
}
