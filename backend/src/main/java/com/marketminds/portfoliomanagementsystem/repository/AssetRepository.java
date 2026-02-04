package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * Find an asset by its stock symbol
     * @param symbol the stock symbol
     * @return Optional containing the asset if found
     */
    Optional<Asset> findBySymbol(String symbol);

    /**
     * Find all assets of a specific type (Stock, Bond, etc.)
     * @param type the asset type
     * @return List of assets of the specified type
     */
    List<Asset> findByType(String type);

    /**
     * Find all assets in a specific sector
     * @param sector the sector name
     * @return List of assets in the specified sector
     */
    List<Asset> findBySector(String sector);

    /**
     * Find assets by both type and sector
     * @param type the asset type
     * @param sector the sector name
     * @return List of assets matching both criteria
     */
    List<Asset> findByTypeAndSector(String type, String sector);
}
