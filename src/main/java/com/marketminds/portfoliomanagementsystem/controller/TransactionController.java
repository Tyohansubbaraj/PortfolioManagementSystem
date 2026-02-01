package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Transaction management
 * Handles all HTTP requests related to transactions
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    private final TransactionService transactionService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create a new transaction
     * POST /api/transactions
     *
     * @param transaction the transaction to create
     * @return ResponseEntity with created transaction and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all transactions
     * GET /api/transactions
     *
     * @return ResponseEntity with list of all transactions
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transaction by ID
     * GET /api/transactions/{id}
     *
     * @param id the transaction ID
     * @return ResponseEntity with transaction if found, otherwise 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        try {
            Optional<Transaction> transaction = transactionService.getTransactionById(id);
            return transaction.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transactions by asset ID
     * GET /api/transactions/asset/{assetId}
     *
     * @param assetId the asset ID
     * @return ResponseEntity with list of transactions for the asset
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAssetId(@PathVariable Long assetId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByAssetId(assetId);
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transactions by asset ID ordered by date (most recent first)
     * GET /api/transactions/asset/{assetId}/sorted
     *
     * @param assetId the asset ID
     * @return ResponseEntity with list of transactions ordered by date
     */
    @GetMapping("/asset/{assetId}/sorted")
    public ResponseEntity<List<Transaction>> getTransactionsByAssetIdOrderByDateDesc(@PathVariable Long assetId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByAssetIdOrderByDateDesc(assetId);
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transactions by type (BUY or SELL)
     * GET /api/transactions/type/{type}
     *
     * @param type the transaction type (BUY or SELL)
     * @return ResponseEntity with list of transactions of the specified type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@PathVariable String type) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByType(type);
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transactions within a date range
     * GET /api/transactions/daterange?startDate={startDate}&endDate={endDate}
     *
     * @param startDate the start date (ISO format)
     * @param endDate the end date (ISO format)
     * @return ResponseEntity with list of transactions within the date range
     */
    @GetMapping("/daterange")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(start, end);
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transactions for a specific asset within a date range
     * GET /api/transactions/asset/{assetId}/daterange?startDate={startDate}&endDate={endDate}
     *
     * @param assetId the asset ID
     * @param startDate the start date (ISO format)
     * @param endDate the end date (ISO format)
     * @return ResponseEntity with list of transactions for the asset within the date range
     */
    @GetMapping("/asset/{assetId}/daterange")
    public ResponseEntity<List<Transaction>> getTransactionsByAssetAndDateRange(
            @PathVariable Long assetId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            List<Transaction> transactions = transactionService.getTransactionsByAssetAndDateRange(assetId, start, end);
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get transaction count for an asset
     * GET /api/transactions/asset/{assetId}/count
     *
     * @param assetId the asset ID
     * @return ResponseEntity with transaction count
     */
    @GetMapping("/asset/{assetId}/count")
    public ResponseEntity<Long> getTransactionCountByAssetId(@PathVariable Long assetId) {
        try {
            long count = transactionService.getTransactionCountByAssetId(assetId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if transactions exist for an asset
     * GET /api/transactions/asset/{assetId}/exists
     *
     * @param assetId the asset ID
     * @return ResponseEntity with boolean indicating existence
     */
    @GetMapping("/asset/{assetId}/exists")
    public ResponseEntity<Boolean> transactionExistsByAssetId(@PathVariable Long assetId) {
        try {
            boolean exists = transactionService.transactionExistsByAssetId(assetId);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing transaction
     * PUT /api/transactions/{id}
     *
     * @param id the transaction ID
     * @param transaction the updated transaction data
     * @return ResponseEntity with updated transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        try {
            if (!id.equals(transaction.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Transaction updatedTransaction = transactionService.updateTransaction(transaction);
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a transaction
     * DELETE /api/transactions/{id}
     *
     * @param id the transaction ID
     * @return ResponseEntity with no content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
