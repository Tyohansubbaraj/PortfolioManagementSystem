package com.marketminds.portfoliomanagementsystem.dto;

import java.math.BigDecimal;

public class HoldingsSummaryDTO {

    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal totalProfitLossPercent;

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getTotalProfitLoss() {
        return totalProfitLoss;
    }

    public void setTotalProfitLoss(BigDecimal totalProfitLoss) {
        this.totalProfitLoss = totalProfitLoss;
    }

    public BigDecimal getTotalProfitLossPercent() {
        return totalProfitLossPercent;
    }

    public void setTotalProfitLossPercent(BigDecimal totalProfitLossPercent) {
        this.totalProfitLossPercent = totalProfitLossPercent;
    }
}
