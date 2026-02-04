package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Transaction service operations
 */
public interface TransactionService {

    /**
     * Create a new transaction
     * @param transaction the transaction to create
     * @return the created transaction
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * Update an existing transaction
     * @param transaction the transaction to update
     * @return the updated transaction
     */
    Transaction updateTransaction(Transaction transaction);

    /**
     * Delete a transaction by ID
     * @param id the transaction ID
     */
    void deleteTransaction(Long id);

    /**
     * Get a transaction by ID
     * @param id the transaction ID
     * @return Optional containing the transaction if found
     */
    Optional<Transaction> getTransactionById(Long id);

    /**
     * Get all transactions
     * @return List of all transactions
     */
    List<Transaction> getAllTransactions();

    /**
     * Find all transactions for a specific asset
     * @param assetId the asset ID
     * @return List of transactions for the asset
     */
    List<Transaction> getTransactionsByAssetId(Long assetId);

    /**
     * Find all transactions for a specific asset ordered by date descending
     * @param assetId the asset ID
     * @return List of transactions ordered by most recent first
     */
    List<Transaction> getTransactionsByAssetIdOrderByDateDesc(Long assetId);

    /**
     * Find all transactions of a specific type (BUY or SELL)
     * @param type the transaction type
     * @return List of transactions of the specified type
     */
    List<Transaction> getTransactionsByType(String type);

    /**
     * Find all transactions within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List of transactions within the date range
     */
    List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all transactions for a specific asset within a date range
     * @param assetId the asset ID
     * @param startDate the start date
     * @param endDate the end date
     * @return List of transactions for the asset within the date range
     */
    List<Transaction> getTransactionsByAssetAndDateRange(Long assetId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get the count of transactions for a specific asset
     * @param assetId the asset ID
     * @return the count of transactions
     */
    long getTransactionCountByAssetId(Long assetId);

    /**
     * Check if any transactions exist for an asset
     * @param assetId the asset ID
     * @return true if transactions exist, false otherwise
     */
    boolean transactionExistsByAssetId(Long assetId);
}
