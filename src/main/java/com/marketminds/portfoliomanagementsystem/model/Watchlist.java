package com.marketminds.portfoliomanagementsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "watchlist")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public Watchlist() {
    }

    public Watchlist(Asset asset, String notes) {
        this.asset = asset;
        this.notes = notes;
    }

    public Watchlist(Long id, Asset asset, String notes) {
        this.id = id;
        this.asset = asset;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // toString() method
    @Override
    public String toString() {
        return "Watchlist{" +
                "id=" + id +
                ", asset=" + asset +
                ", notes='" + notes + '\'' +
                '}';
    }
}
