package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.dto.HoldingDetailsDTO;
import com.marketminds.portfoliomanagementsystem.dto.HoldingsSummaryDTO;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.service.HoldingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Holding management
 * Handles all HTTP requests related to holdings
 */
@RestController
@RequestMapping("/api/holdings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    /**
     * Create a new holding
     * POST /api/holdings
     *
     * @param holding the holding to create
     * @return ResponseEntity with created holding and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Holding> createHolding(@RequestBody Holding holding) {
        try {
            Holding createdHolding = holdingService.createHolding(holding);
            return new ResponseEntity<>(createdHolding, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all holdings
     * GET /api/holdings
     *
     * @return ResponseEntity with list of all holdings
     */
    @GetMapping
    public ResponseEntity<List<Holding>> getAllHoldings() {
        try {
            List<Holding> holdings = holdingService.getAllHoldings();
            if (holdings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(holdings, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get holding by ID
     * GET /api/holdings/{id}
     *
     * @param id the holding ID
     * @return ResponseEntity with holding if found, otherwise 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<HoldingDetailsDTO> getHoldingById(@PathVariable Long id) {
        try {
            HoldingDetailsDTO holding = holdingService.getHoldingById(id);
            return new ResponseEntity<>(holding,HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    /**
     * Get holding by asset ID
     * GET /api/holdings/asset/{assetId}
     *
     * @param assetId the asset ID
     * @return ResponseEntity with holding if found
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Holding> getHoldingByAssetId(@PathVariable Long assetId) {
        try {
            Optional<Holding> holding = holdingService.getHoldingByAssetId(assetId);
            return holding.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if holding exists for an asset
     * GET /api/holdings/exists/{assetId}
     *
     * @param assetId the asset ID
     * @return ResponseEntity with boolean indicating existence
     */
    @GetMapping("/exists/{assetId}")
    public ResponseEntity<Boolean> holdingExistsByAssetId(@PathVariable Long assetId) {
        try {
            boolean exists = holdingService.holdingExistsByAssetId(assetId);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing holding
     * PUT /api/holdings/{id}
     *
     * @param id the holding ID
     * @param holding the updated holding data
     * @return ResponseEntity with updated holding
     */
    @PutMapping("/{id}")
    public ResponseEntity<Holding> updateHolding(@PathVariable Long id, @RequestBody Holding holding) {
        try {
            if (!id.equals(holding.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Holding updatedHolding = holdingService.updateHolding(holding);
            return new ResponseEntity<>(updatedHolding, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update holding on buy - recalculates average buy price
     * POST /api/holdings/{id}/buy
     *
     * @param id the holding ID
     * @param quantity the quantity to buy
     * @param price the price per unit
     * @return ResponseEntity with updated holding
     */
    @PostMapping("/{id}/buy")
    public ResponseEntity<Holding> updateHoldingOnBuy(
            @PathVariable Long id,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal price) {
        try {
            Holding updatedHolding = holdingService.updateHoldingOnBuy(id, quantity, price);
            return new ResponseEntity<>(updatedHolding, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update holding on sell - reduces quantity
     * POST /api/holdings/{id}/sell
     *
     * @param id the holding ID
     * @param quantity the quantity to sell
     * @return ResponseEntity with updated holding
     */
    @PostMapping("/{id}/sell")
    public ResponseEntity<Holding> updateHoldingOnSell(
            @PathVariable Long id,
            @RequestParam BigDecimal quantity) {
        try {
            Holding updatedHolding = holdingService.updateHoldingOnSell(id, quantity);
            return new ResponseEntity<>(updatedHolding, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a holding
     * DELETE /api/holdings/{id}
     *
     * @param id the holding ID
     * @return ResponseEntity with no content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHolding(@PathVariable Long id) {
        try {
            holdingService.deleteHolding(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/summary")
//    public ResponseEntity<HoldingsSummaryDTO> getHoldingsSummary() {
//        return ResponseEntity.ok(holdingService.getHoldingsSummary());
//    }
}
