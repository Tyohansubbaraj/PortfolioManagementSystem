package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.service.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Asset management
 * Handles all HTTP requests related to assets
 */
@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * Create a new asset
     * POST /api/assets
     *
     * @param asset the asset to create
     * @return ResponseEntity with created asset and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        try {
            Asset createdAsset = assetService.createAsset(asset);
            return new ResponseEntity<>(createdAsset, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all assets
     * GET /api/assets
     *
     * @return ResponseEntity with list of all assets
     */
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        try {
            List<Asset> assets = assetService.getAllAssets();
            if (assets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get asset by ID
     * GET /api/assets/{id}
     *
     * @param id the asset ID
     * @return ResponseEntity with asset if found, otherwise 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long id) {
        try {
            Optional<Asset> asset = assetService.getAssetById(id);
            return asset.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get asset by symbol
     * GET /api/assets/symbol/{symbol}
     *
     * @param symbol the stock symbol
     * @return ResponseEntity with asset if found
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Asset> getAssetBySymbol(@PathVariable String symbol) {
        try {
            Optional<Asset> asset = assetService.getAssetBySymbol(symbol);
            return asset.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get assets by type
     * GET /api/assets/type/{type}
     *
     * @param type the asset type (Stock, Bond, etc.)
     * @return ResponseEntity with list of assets of specified type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Asset>> getAssetsByType(@PathVariable String type) {
        try {
            List<Asset> assets = assetService.getAssetsByType(type);
            if (assets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get assets by sector
     * GET /api/assets/sector/{sector}
     *
     * @param sector the sector name
     * @return ResponseEntity with list of assets in specified sector
     */
    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Asset>> getAssetsBySector(@PathVariable String sector) {
        try {
            List<Asset> assets = assetService.getAssetsBySector(sector);
            if (assets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get assets by type and sector
     * GET /api/assets/type/{type}/sector/{sector}
     *
     * @param type the asset type
     * @param sector the sector name
     * @return ResponseEntity with list of assets matching both criteria
     */
    @GetMapping("/type/{type}/sector/{sector}")
    public ResponseEntity<List<Asset>> getAssetsByTypeAndSector(
            @PathVariable String type,
            @PathVariable String sector) {
        try {
            List<Asset> assets = assetService.getAssetsByTypeAndSector(type, sector);
            if (assets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing asset
     * PUT /api/assets/{id}
     *
     * @param id the asset ID
     * @param asset the updated asset data
     * @return ResponseEntity with updated asset
     */
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        try {
            if (!id.equals(asset.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Asset updatedAsset = assetService.updateAsset(asset);
            return new ResponseEntity<>(updatedAsset, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete an asset
     * DELETE /api/assets/{id}
     *
     * @param id the asset ID
     * @return ResponseEntity with no content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        try {
            assetService.deleteAsset(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if asset exists by symbol
     * GET /api/assets/exists/{symbol}
     *
     * @param symbol the stock symbol
     * @return ResponseEntity with boolean indicating existence
     */
    @GetMapping("/exists/{symbol}")
    public ResponseEntity<Boolean> assetExistsBySymbol(@PathVariable String symbol) {
        try {
            boolean exists = assetService.assetExistsBySymbol(symbol);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
