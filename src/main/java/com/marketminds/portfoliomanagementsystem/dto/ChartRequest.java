package com.marketminds.portfoliomanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for chart data request
 * Used to request chart data from the Python price service
 */
public class ChartRequest {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("start_date")
    private String startDate;

    // Constructors
    public ChartRequest() {
    }

    public ChartRequest(String symbol, String startDate) {
        this.symbol = symbol;
        this.startDate = startDate;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "ChartRequest{" +
                "symbol='" + symbol + '\'' +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
