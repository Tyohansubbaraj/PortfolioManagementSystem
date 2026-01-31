package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Asset asset1;
    private Asset asset2;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        // Create and persist assets
        asset1 = new Asset("AAPL", "Apple Inc.", "Stock", "Technology", new BigDecimal("150.25"), LocalDateTime.now());
        asset2 = new Asset("MSFT", "Microsoft Corporation", "Stock", "Technology", new BigDecimal("320.50"), LocalDateTime.now());

        entityManager.persist(asset1);
        entityManager.persist(asset2);

        // Create and persist transactions
        transaction1 = new Transaction(asset1, "BUY", new BigDecimal("100.0000"), new BigDecimal("145.00"), LocalDateTime.now().minusDays(10));
        transaction2 = new Transaction(asset1, "SELL", new BigDecimal("50.0000"), new BigDecimal("155.00"), LocalDateTime.now().minusDays(5));
        transaction3 = new Transaction(asset2, "BUY", new BigDecimal("25.0000"), new BigDecimal("310.00"), LocalDateTime.now().minusDays(3));

        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.persist(transaction3);
        entityManager.flush();
    }

    @Test
    void testFindByAssetId_Success() {
        // Act
        List<Transaction> results = transactionRepository.findByAssetId(asset1.getId());

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getAsset().getId().equals(asset1.getId())));
    }

    @Test
    void testFindByAssetId_NoResults() {
        // Act
        List<Transaction> results = transactionRepository.findByAssetId(999L);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByAssetIdOrderByTradeDateDesc_Success() {
        // Act
        List<Transaction> results = transactionRepository.findByAssetIdOrderByTradeDateDesc(asset1.getId());

        // Assert
        assertEquals(2, results.size());
        // Most recent first
        assertTrue(results.get(0).getTradeDate().isAfter(results.get(1).getTradeDate()));
    }

    @Test
    void testFindByType_BuyTransactions() {
        // Act
        List<Transaction> buyTransactions = transactionRepository.findByType("BUY");

        // Assert
        assertEquals(2, buyTransactions.size());
        assertTrue(buyTransactions.stream().allMatch(t -> "BUY".equals(t.getType())));
    }

    @Test
    void testFindByType_SellTransactions() {
        // Act
        List<Transaction> sellTransactions = transactionRepository.findByType("SELL");

        // Assert
        assertEquals(1, sellTransactions.size());
        assertTrue(sellTransactions.stream().allMatch(t -> "SELL".equals(t.getType())));
    }

    @Test
    void testFindByType_NoResults() {
        // Act
        List<Transaction> results = transactionRepository.findByType("UNKNOWN");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindTransactionsByDateRange_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(12);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2);

        // Act
        List<Transaction> results = transactionRepository.findTransactionsByDateRange(startDate, endDate);

        // Assert
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(t ->
            t.getTradeDate().isAfter(startDate) && t.getTradeDate().isBefore(endDate)));
    }

    @Test
    void testFindTransactionsByDateRange_NoResults() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        // Act
        List<Transaction> results = transactionRepository.findTransactionsByDateRange(startDate, endDate);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindTransactionsByAssetAndDateRange_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(12);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        // Act
        List<Transaction> results = transactionRepository.findTransactionsByAssetAndDateRange(asset1.getId(), startDate, endDate);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t ->
            t.getAsset().getId().equals(asset1.getId()) &&
            t.getTradeDate().isAfter(startDate) &&
            t.getTradeDate().isBefore(endDate)));
    }

    @Test
    void testFindTransactionsByAssetAndDateRange_NoResults() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        // Act
        List<Transaction> results = transactionRepository.findTransactionsByAssetAndDateRange(asset1.getId(), startDate, endDate);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testSaveTransaction() {
        // Arrange
        Transaction newTransaction = new Transaction(asset2, "SELL", new BigDecimal("10.0000"), new BigDecimal("325.00"), LocalDateTime.now());

        // Act
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        entityManager.flush();

        // Assert
        assertNotNull(savedTransaction.getId());
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(savedTransaction.getId());
        assertTrue(retrievedTransaction.isPresent());
        assertEquals("SELL", retrievedTransaction.get().getType());
    }

    @Test
    void testUpdateTransaction() {
        // Arrange
        Long transactionId = transaction1.getId();
        Optional<Transaction> foundTransaction = transactionRepository.findById(transactionId);
        assertTrue(foundTransaction.isPresent());
        Transaction transactionToUpdate = foundTransaction.get();

        // Act
        transactionToUpdate.setPrice(new BigDecimal("150.00"));
        transactionRepository.save(transactionToUpdate);
        entityManager.flush();

        // Assert
        Optional<Transaction> updatedTransaction = transactionRepository.findById(transactionId);
        assertTrue(updatedTransaction.isPresent());
        assertEquals(new BigDecimal("150.00"), updatedTransaction.get().getPrice());
    }

    @Test
    void testDeleteTransaction() {
        // Arrange
        Long transactionId = transaction2.getId();
        Optional<Transaction> foundTransaction = transactionRepository.findById(transactionId);
        assertTrue(foundTransaction.isPresent());

        // Act
        transactionRepository.delete(foundTransaction.get());
        entityManager.flush();

        // Assert
        Optional<Transaction> deletedTransaction = transactionRepository.findById(transactionId);
        assertFalse(deletedTransaction.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Long transactionId = transaction3.getId();

        // Act
        transactionRepository.deleteById(transactionId);
        entityManager.flush();

        // Assert
        Optional<Transaction> deletedTransaction = transactionRepository.findById(transactionId);
        assertFalse(deletedTransaction.isPresent());
    }

    @Test
    void testFindById() {
        // Arrange
        Long transactionId = transaction1.getId();

        // Act
        Optional<Transaction> result = transactionRepository.findById(transactionId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("BUY", result.get().getType());
        assertEquals(asset1.getId(), result.get().getAsset().getId());
    }

    @Test
    void testFindAll() {
        // Act
        var allTransactions = transactionRepository.findAll();

        // Assert
        assertEquals(3, allTransactions.size());
    }
}
