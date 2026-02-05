package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Watchlist;
import com.marketminds.portfoliomanagementsystem.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Watchlist management
 * Handles all HTTP requests related to watchlist
 */
@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    /**
     * Create a new watchlist entry
     * POST /api/watchlist
     *
     * @param watchlist the watchlist entry to create
     * @return ResponseEntity with created watchlist entry and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Watchlist> createWatchlistEntry(@RequestBody Watchlist watchlist) {
        try {
            Watchlist createdWatchlist = watchlistService.createWatchlistEntry(watchlist);
            return new ResponseEntity<>(createdWatchlist, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all watchlist entries
     * GET /api/watchlist
     *
     * @return ResponseEntity with list of all watchlist entries
     */
    @GetMapping
    public ResponseEntity<List<Watchlist>> getAllWatchlistEntries() {
        try {
            List<Watchlist> watchlists = watchlistService.getAllWatchlistEntries();
            if (watchlists.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(watchlists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get watchlist entry by ID
     * GET /api/watchlist/{id}
     *
     * @param id the watchlist entry ID
     * @return ResponseEntity with watchlist entry if found, otherwise 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Watchlist> getWatchlistEntryById(@PathVariable Long id) {
        try {
            Optional<Watchlist> watchlist = watchlistService.getWatchlistEntryById(id);
            return watchlist.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get watchlist entry by asset ID
     * GET /api/watchlist/asset/{assetId}
     *
     * @param assetId the asset ID
     * @return ResponseEntity with watchlist entry if found
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Watchlist> getWatchlistEntryByAssetId(@PathVariable Long assetId) {
        try {
            Optional<Watchlist> watchlist = watchlistService.getWatchlistEntryByAssetId(assetId);
            return watchlist.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if an asset is in the watchlist
     * GET /api/watchlist/asset/{assetId}/exists
     *
     * @param assetId the asset ID
     * @return ResponseEntity with boolean indicating if asset is in watchlist
     */
    @GetMapping("/asset/{assetId}/exists")
    public ResponseEntity<Boolean> isAssetInWatchlist(@PathVariable Long assetId) {
        try {
            boolean inWatchlist = watchlistService.isAssetInWatchlist(assetId);
            return new ResponseEntity<>(inWatchlist, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add an asset to the watchlist
     * POST /api/watchlist/asset/{assetId}
     *
     * @param assetId the asset ID
     * @param notes optional notes for the watchlist entry
     * @return ResponseEntity with created watchlist entry
     */
    @PostMapping("/asset/{assetId}")
    public ResponseEntity<Watchlist> addAssetToWatchlist(
            @PathVariable Long assetId,
            @RequestParam(required = false) String notes) {
        try {
            Watchlist watchlist = watchlistService.addAssetToWatchlist(assetId, notes);
            return new ResponseEntity<>(watchlist, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove an asset from the watchlist
     * DELETE /api/watchlist/asset/{assetId}
     *
     * @param assetId the asset ID
     * @return ResponseEntity with no content on success
     */
    @DeleteMapping("/asset/{assetId}")
    public ResponseEntity<Void> removeAssetFromWatchlist(@PathVariable Long assetId) {
        try {
            watchlistService.removeAssetFromWatchlist(assetId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing watchlist entry
     * PUT /api/watchlist/{id}
     *
     * @param id the watchlist entry ID
     * @param watchlist the updated watchlist entry data
     * @return ResponseEntity with updated watchlist entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<Watchlist> updateWatchlistEntry(@PathVariable Long id, @RequestBody Watchlist watchlist) {
        try {
            if (!id.equals(watchlist.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Watchlist updatedWatchlist = watchlistService.updateWatchlistEntry(watchlist);
            return new ResponseEntity<>(updatedWatchlist, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a watchlist entry
     * DELETE /api/watchlist/{id}
     *
     * @param id the watchlist entry ID
     * @return ResponseEntity with no content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlistEntry(@PathVariable Long id) {
        try {
            watchlistService.deleteWatchlistEntry(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the count of assets in watchlist
     * GET /api/watchlist/count
     *
     * @return ResponseEntity with count of watchlist entries
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getWatchlistCount() {
        try {
            long count = watchlistService.getWatchlistCount();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
