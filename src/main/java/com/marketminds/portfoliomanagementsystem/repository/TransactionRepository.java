package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a specific asset
     * @param assetId the asset ID
     * @return List of transactions for the asset
     */
    List<Transaction> findByAssetId(Long assetId);

    /**
     * Find all transactions for a specific asset ordered by date descending
     * @param assetId the asset ID
     * @return List of transactions ordered by most recent first
     */
    List<Transaction> findByAssetIdOrderByTradeDateDesc(Long assetId);

    /**
     * Find all transactions of a specific type (BUY or SELL)
     * @param type the transaction type
     * @return List of transactions of the specified type
     */
    List<Transaction> findByType(String type);

    /**
     * Find all transactions within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List of transactions within the date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.tradeDate BETWEEN :startDate AND :endDate ORDER BY t.tradeDate DESC")
    List<Transaction> findTransactionsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find all transactions for a specific asset within a date range
     * @param assetId the asset ID
     * @param startDate the start date
     * @param endDate the end date
     * @return List of transactions for the asset within the date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.asset.id = :assetId AND t.tradeDate BETWEEN :startDate AND :endDate ORDER BY t.tradeDate DESC")
    List<Transaction> findTransactionsByAssetAndDateRange(@Param("assetId") Long assetId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // --- Pageable / Page variants ---
    Page<Transaction> findByAssetId(Long assetId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.tradeDate BETWEEN :startDate AND :endDate ORDER BY t.tradeDate DESC")
    Page<Transaction> findTransactionsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.asset.id = :assetId AND t.tradeDate BETWEEN :startDate AND :endDate ORDER BY t.tradeDate DESC")
    Page<Transaction> findTransactionsByAssetAndDateRange(@Param("assetId") Long assetId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
