package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.dto.HoldingDetailsDTO;
import com.marketminds.portfoliomanagementsystem.model.Holding;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Holding service operations
 */
public interface HoldingService {

    /**
     * Create a new holding
     *
     * @param holding the holding to create
     * @return the created holding
     */
    Holding createHolding(Holding holding);

    /**
     * Update an existing holding
     *
     * @param holding the holding to update
     * @return the updated holding
     */
    Holding updateHolding(Holding holding);

    /**
     * Delete a holding by ID
     *
     * @param id the holding ID
     */
    void deleteHolding(Long id);

    /**
     * Get a holding by ID
     *
     * @param id the holding ID
     * @return Optional containing the holding if found
     */
    HoldingDetailsDTO getHoldingById(Long id);

    /**
     * Get all holdings
     *
     * @return List of all holdings
     */
    List<Holding> getAllHoldings();

    /**
     * Get a holding by asset ID
     *
     * @param assetId the asset ID
     * @return Optional containing the holding if found
     */
    Optional<Holding> getHoldingByAssetId(Long assetId);

    /**
     * Check if a holding exists for an asset
     *
     * @param assetId the asset ID
     * @return true if holding exists, false otherwise
     */
    boolean holdingExistsByAssetId(Long assetId);

    /**
     * Update the total quantity and average buy price of a holding
     *
     * @param holdingId   the holding ID
     * @param newQuantity the new quantity to add
     * @param newPrice    the price of the new purchase
     * @return the updated holding
     */
    Holding updateHoldingOnBuy(Long holdingId, BigDecimal newQuantity, BigDecimal newPrice);

    /**
     * Update the total quantity of a holding on sell
     *
     * @param holdingId      the holding ID
     * @param quantityToSell the quantity to sell
     * @return the updated holding
     */
    Holding updateHoldingOnSell(Long holdingId, BigDecimal quantityToSell);
}

//    /**
//     * Get a summary of all holdings
//     * @return HoldingsSummaryDTO containing the summary
//     */
//    HoldingsSummaryDTO getHoldingsSummary();
//}
