package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    /**
     * Find a watchlist entry by asset ID
     * @param assetId the asset ID
     * @return Optional containing the watchlist entry if found
     */
    Optional<Watchlist> findByAssetId(Long assetId);

    /**
     * Check if an asset is in the watchlist
     * @param assetId the asset ID
     * @return true if the asset is in the watchlist, false otherwise
     */
    boolean existsByAssetId(Long assetId);
}
