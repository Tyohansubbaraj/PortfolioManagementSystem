package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.dto.ChartPoint;
import com.marketminds.portfoliomanagementsystem.dto.HoldingDetailsDTO;
import com.marketminds.portfoliomanagementsystem.service.HoldingService;
import com.marketminds.portfoliomanagementsystem.service.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST Controller for Chart data management
 * Handles chart data requests for holdings
 */
@RestController
@RequestMapping("/api/holdings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChartController {

    private final HoldingService holdingService;
    private final PriceService priceService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ChartController(HoldingService holdingService, PriceService priceService) {
        this.holdingService = holdingService;
        this.priceService = priceService;
    }

    /**
     * Get chart data for a holding from its purchase date to today
     * GET /api/holdings/{holdingId}/chart
     *
     * @param holdingId the ID of the holding
     * @return ResponseEntity with list of ChartPoint objects
     */
    @GetMapping("/{holdingId}/chart")
    public ResponseEntity<List<ChartPoint>> getHoldingChart(@PathVariable Long holdingId) {
        try {
            // Fetch the holding by ID
            HoldingDetailsDTO holding = holdingService.getHoldingById(holdingId);

            // Check if holding exists
            if (holding == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Get the symbol from the associated asset
            String symbol = holding.getAssetSymbol();

            // Convert buyDate to yyyy-MM-dd format
            String startDate = holding.getBuyDate().format(DATE_FORMATTER);

            // Fetch chart data from PriceService
            List<ChartPoint> chartData = priceService.fetchChartData(symbol, startDate);

            return new ResponseEntity<>(chartData, HttpStatus.OK);

        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
