package com.marketminds.portfoliomanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Data Transfer Object for chart data points
 * Represents a single price point on the chart
 */
public class ChartPoint {

    @JsonProperty("date")
    private String date;

    @JsonProperty("price")
    private BigDecimal price;

    // Constructors
    public ChartPoint() {
    }

    public ChartPoint(String date, BigDecimal price) {
        this.date = date;
        this.price = price;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ChartPoint{" +
                "date='" + date + '\'' +
                ", price=" + price +
                '}';
    }
}
