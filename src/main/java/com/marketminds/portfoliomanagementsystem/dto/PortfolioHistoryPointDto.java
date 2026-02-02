package com.marketminds.portfoliomanagementsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PortfolioHistoryPointDto {
    private LocalDate date;
    private BigDecimal totalValue;

    public PortfolioHistoryPointDto(LocalDate date, BigDecimal totalValue) {
        this.date = date;
        this.totalValue = totalValue;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }
}
