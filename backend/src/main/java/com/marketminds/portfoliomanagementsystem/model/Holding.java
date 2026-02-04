package com.marketminds.portfoliomanagementsystem.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", unique = true, nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Asset asset;

    @Column(name = "total_quantity", precision = 18, scale = 4, nullable = false)
    private BigDecimal totalQuantity;

    @Column(name = "avg_buy_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal avgBuyPrice;

    @Column(name = "buy_date", nullable = false)
    private LocalDate buyDate;

    // Constructors
    public Holding() {
    }

    public Holding(Asset asset, BigDecimal totalQuantity, BigDecimal avgBuyPrice, LocalDate buyDate) {
        this.asset = asset;
        this.totalQuantity = totalQuantity;
        this.avgBuyPrice = avgBuyPrice;
        this.buyDate = buyDate;
    }

    public Holding(Long id, Asset asset, BigDecimal totalQuantity, BigDecimal avgBuyPrice, LocalDate buyDate) {
        this.id = id;
        this.asset = asset;
        this.totalQuantity = totalQuantity;
        this.avgBuyPrice = avgBuyPrice;
        this.buyDate = buyDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(BigDecimal avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(LocalDate buyDate) {
        this.buyDate = buyDate;
    }

    // toString() method
    @Override
    public String toString() {
        return "Holding{" +
                "id=" + id +
                ", asset=" + asset +
                ", totalQuantity=" + totalQuantity +
                ", avgBuyPrice=" + avgBuyPrice +
                ", buyDate=" + buyDate +
                '}';
    }
}
