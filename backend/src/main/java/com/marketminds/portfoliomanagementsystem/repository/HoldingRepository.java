package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    /**
     * Find a holding by its asset ID
     * @param assetId the asset ID
     * @return Optional containing the holding if found
     */
    Optional<Holding> findByAssetId(Long assetId);

    /**
     * Find all holdings (since each asset has one holding)
     * @return List of all holdings
     */
}
