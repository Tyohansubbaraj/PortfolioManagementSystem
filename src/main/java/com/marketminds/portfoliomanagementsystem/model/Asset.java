package com.marketminds.portfoliomanagementsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", length = 10, unique = true, nullable = false)
    private String symbol;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(name = "sector", length = 50, nullable = false)
    private String sector;

    @Column(name = "current_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "last_updated", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdated;

    // Constructors
    public Asset() {
    }

    public Asset(String symbol, String name, String type, String sector, BigDecimal currentPrice, LocalDateTime lastUpdated) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.sector = sector;
        this.currentPrice = currentPrice;
        this.lastUpdated = lastUpdated;
    }

    public Asset(Long id, String symbol, String name, String type, String sector, BigDecimal currentPrice, LocalDateTime lastUpdated) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.sector = sector;
        this.currentPrice = currentPrice;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // toString() method
    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", sector='" + sector + '\'' +
                ", currentPrice=" + currentPrice +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
