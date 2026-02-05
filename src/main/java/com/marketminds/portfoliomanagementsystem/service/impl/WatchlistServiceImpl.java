package com.marketminds.portfoliomanagementsystem.service.impl;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Watchlist;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.repository.WatchlistRepository;
import com.marketminds.portfoliomanagementsystem.service.WatchlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    private static final Logger log = LoggerFactory.getLogger(WatchlistServiceImpl.class);
    private final WatchlistRepository watchlistRepository;
    private final AssetRepository assetRepository;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, AssetRepository assetRepository) {
        this.watchlistRepository = watchlistRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public Watchlist createWatchlistEntry(Watchlist watchlist) {
        if (watchlist == null) {
            throw new IllegalArgumentException("Watchlist entry cannot be null");
        }
        if (watchlist.getAsset() == null) {
            throw new IllegalArgumentException("Watchlist asset cannot be null");
        }
        if (watchlistRepository.findByAssetId(watchlist.getAsset().getId()).isPresent()) {
            throw new IllegalArgumentException("Asset is already in the watchlist");
        }
        log.info("Creating new watchlist entry for asset ID: {}", watchlist.getAsset().getId());
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist updateWatchlistEntry(Watchlist watchlist) {
        if (watchlist == null || watchlist.getId() == null) {
            throw new IllegalArgumentException("Watchlist entry and entry ID cannot be null");
        }
        if (!watchlistRepository.existsById(watchlist.getId())) {
            throw new IllegalArgumentException("Watchlist entry with ID " + watchlist.getId() + " does not exist");
        }
        log.info("Updating watchlist entry with ID: {}", watchlist.getId());
        return watchlistRepository.save(watchlist);
    }

    @Override
    public void deleteWatchlistEntry(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Watchlist entry ID cannot be null");
        }
        if (!watchlistRepository.existsById(id)) {
            throw new IllegalArgumentException("Watchlist entry with ID " + id + " does not exist");
        }
        log.info("Deleting watchlist entry with ID: {}", id);
        watchlistRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Watchlist> getWatchlistEntryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Watchlist entry ID cannot be null");
        }
        log.info("Fetching watchlist entry with ID: {}", id);
        return watchlistRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Watchlist> getAllWatchlistEntries() {
        log.info("Fetching all watchlist entries");
        return watchlistRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Watchlist> getWatchlistEntryByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        log.info("Fetching watchlist entry for asset ID: {}", assetId);
        return watchlistRepository.findByAssetId(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAssetInWatchlist(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        log.info("Checking if asset ID {} is in watchlist", assetId);
        return watchlistRepository.existsByAssetId(assetId);
    }

    @Override
    public void removeAssetFromWatchlist(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        Optional<Watchlist> watchlistOptional = watchlistRepository.findByAssetId(assetId);
        if (!watchlistOptional.isPresent()) {
            throw new IllegalArgumentException("Asset with ID " + assetId + " is not in the watchlist");
        }
        log.info("Removing asset ID {} from watchlist", assetId);
        watchlistRepository.deleteById(watchlistOptional.get().getId());
    }

    @Override
    public Watchlist addAssetToWatchlist(Long assetId, String notes) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }

        Optional<Asset> assetOptional = assetRepository.findById(assetId);
        if (!assetOptional.isPresent()) {
            throw new IllegalArgumentException("Asset with ID " + assetId + " does not exist");
        }

        if (watchlistRepository.existsByAssetId(assetId)) {
            throw new IllegalArgumentException("Asset is already in the watchlist");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setAsset(assetOptional.get());
        watchlist.setNotes(notes);

        log.info("Adding asset ID {} to watchlist", assetId);
        return watchlistRepository.save(watchlist);
    }

    @Override
    @Transactional(readOnly = true)
    public long getWatchlistCount() {
        log.info("Fetching watchlist count");
        return watchlistRepository.findAll().size();
    }
}
