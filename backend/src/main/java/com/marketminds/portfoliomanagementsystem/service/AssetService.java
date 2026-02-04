package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Asset;

import java.util.List;
import java.util.Optional;

/**
 * Interface for Asset service operations
 */
public interface AssetService {

    /**
     * Create a new asset
     * @param asset the asset to create
     * @return the created asset
     */
    Asset createAsset(Asset asset);

    /**
     * Update an existing asset
     * @param asset the asset to update
     * @return the updated asset
     */
    Asset updateAsset(Asset asset);

    /**
     * Delete an asset by ID
     * @param id the asset ID
     */
    void deleteAsset(Long id);

    /**
     * Get an asset by ID
     * @param id the asset ID
     * @return Optional containing the asset if found
     */
    Optional<Asset> getAssetById(Long id);

    /**
     * Get all assets
     * @return List of all assets
     */
    List<Asset> getAllAssets();

    /**
     * Find an asset by symbol
     * @param symbol the stock symbol
     * @return Optional containing the asset if found
     */
    Optional<Asset> getAssetBySymbol(String symbol);

    /**
     * Find all assets by type
     * @param type the asset type (Stock, Bond, etc.)
     * @return List of assets of the specified type
     */
    List<Asset> getAssetsByType(String type);

    /**
     * Find all assets by sector
     * @param sector the sector name
     * @return List of assets in the specified sector
     */
    List<Asset> getAssetsBySector(String sector);

    /**
     * Find assets by type and sector
     * @param type the asset type
     * @param sector the sector name
     * @return List of assets matching both criteria
     */
    List<Asset> getAssetsByTypeAndSector(String type, String sector);

    /**
     * Check if an asset exists by symbol
     * @param symbol the stock symbol
     * @return true if asset exists, false otherwise
     */
    boolean assetExistsBySymbol(String symbol);
}
