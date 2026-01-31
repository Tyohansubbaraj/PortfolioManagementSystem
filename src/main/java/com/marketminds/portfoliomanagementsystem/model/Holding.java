package com.marketminds.portfoliomanagementsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", unique = true, nullable = false)
    private Asset asset;

    @Column(name = "total_quantity", precision = 18, scale = 4, nullable = false)
    private BigDecimal totalQuantity;

    @Column(name = "avg_buy_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal avgBuyPrice;

    // Constructors
    public Holding() {
    }

    public Holding(Asset asset, BigDecimal totalQuantity, BigDecimal avgBuyPrice) {
        this.asset = asset;
        this.totalQuantity = totalQuantity;
        this.avgBuyPrice = avgBuyPrice;
    }

    public Holding(Long id, Asset asset, BigDecimal totalQuantity, BigDecimal avgBuyPrice) {
        this.id = id;
        this.asset = asset;
        this.totalQuantity = totalQuantity;
        this.avgBuyPrice = avgBuyPrice;
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

    // toString() method
    @Override
    public String toString() {
        return "Holding{" +
                "id=" + id +
                ", asset=" + asset +
                ", totalQuantity=" + totalQuantity +
                ", avgBuyPrice=" + avgBuyPrice +
                '}';
    }
}
