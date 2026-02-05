package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Watchlist;

import java.util.List;
import java.util.Optional;

/**
 * Interface for Watchlist service operations
 */
public interface WatchlistService {

    /**
     * Create a new watchlist entry
     * @param watchlist the watchlist entry to create
     * @return the created watchlist entry
     */
    Watchlist createWatchlistEntry(Watchlist watchlist);

    /**
     * Update an existing watchlist entry
     * @param watchlist the watchlist entry to update
     * @return the updated watchlist entry
     */
    Watchlist updateWatchlistEntry(Watchlist watchlist);

    /**
     * Delete a watchlist entry by ID
     * @param id the watchlist entry ID
     */
    void deleteWatchlistEntry(Long id);

    /**
     * Get a watchlist entry by ID
     * @param id the watchlist entry ID
     * @return Optional containing the watchlist entry if found
     */
    Optional<Watchlist> getWatchlistEntryById(Long id);

    /**
     * Get all watchlist entries
     * @return List of all watchlist entries
     */
    List<Watchlist> getAllWatchlistEntries();

    /**
     * Get a watchlist entry by asset ID
     * @param assetId the asset ID
     * @return Optional containing the watchlist entry if found
     */
    Optional<Watchlist> getWatchlistEntryByAssetId(Long assetId);

    /**
     * Check if an asset is in the watchlist
     * @param assetId the asset ID
     * @return true if asset is in watchlist, false otherwise
     */
    boolean isAssetInWatchlist(Long assetId);

    /**
     * Remove an asset from the watchlist
     * @param assetId the asset ID
     */
    void removeAssetFromWatchlist(Long assetId);

    /**
     * Add an asset to the watchlist with notes
     * @param assetId the asset ID
     * @param notes the notes for the watchlist entry
     * @return the created watchlist entry
     */
    Watchlist addAssetToWatchlist(Long assetId, String notes);

    /**
     * Get the count of assets in the watchlist
     * @return the count of watchlist entries
     */
    long getWatchlistCount();
}
