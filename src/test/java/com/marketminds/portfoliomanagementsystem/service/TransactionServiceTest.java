package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.repository.TransactionRepository;
import com.marketminds.portfoliomanagementsystem.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Transaction Service Unit Tests")
class TransactionServiceTest {

    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionServiceImpl(transactionRepository);

        testAsset = new Asset();
        testAsset.setId(1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType("Stock");
        testAsset.setSector("Technology");
        testAsset.setCurrentPrice(new BigDecimal("150.50"));
        testAsset.setLastUpdated(LocalDateTime.now());

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAsset(testAsset);
        testTransaction.setType("BUY");
        testTransaction.setQuantity(new BigDecimal("10.00"));
        testTransaction.setPrice(new BigDecimal("150.50"));
        testTransaction.setTradeDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create a new BUY transaction successfully")
    void testCreateTransaction_BuySuccess() {
        when(transactionRepository.save(testTransaction)).thenReturn(testTransaction);

        Transaction result = transactionService.createTransaction(testTransaction);

        assertNotNull(result);
        assertEquals("BUY", result.getType());
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    @DisplayName("Should create a new SELL transaction successfully")
    void testCreateTransaction_SellSuccess() {
        testTransaction.setType("SELL");
        when(transactionRepository.save(testTransaction)).thenReturn(testTransaction);

        Transaction result = transactionService.createTransaction(testTransaction);

        assertNotNull(result);
        assertEquals("SELL", result.getType());
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with null")
    void testCreateTransaction_NullTransaction() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(null);
        });
        assertEquals("Transaction cannot be null", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with null asset")
    void testCreateTransaction_NullAsset() {
        testTransaction.setAsset(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(testTransaction);
        });
        assertEquals("Transaction asset cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with invalid type")
    void testCreateTransaction_InvalidType() {
        testTransaction.setType("INVALID");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(testTransaction);
        });
        assertEquals("Transaction type must be either BUY or SELL", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with invalid quantity")
    void testCreateTransaction_InvalidQuantity() {
        testTransaction.setQuantity(BigDecimal.ZERO);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(testTransaction);
        });
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with invalid price")
    void testCreateTransaction_InvalidPrice() {
        testTransaction.setPrice(new BigDecimal("-10.00"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(testTransaction);
        });
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should update an existing transaction successfully")
    void testUpdateTransaction_Success() {
        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(testTransaction)).thenReturn(testTransaction);

        Transaction result = transactionService.updateTransaction(testTransaction);

        assertNotNull(result);
        verify(transactionRepository, times(1)).existsById(1L);
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent transaction")
    void testUpdateTransaction_NonExistent() {
        when(transactionRepository.existsById(999L)).thenReturn(false);
        testTransaction.setId(999L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.updateTransaction(testTransaction);
        });
        assertEquals("Transaction with ID 999 does not exist", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete a transaction successfully")
    void testDeleteTransaction_Success() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.deleteTransaction(1L);

        verify(transactionRepository, times(1)).existsById(1L);
        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should retrieve transaction by ID successfully")
    void testGetTransactionById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionService.getTransactionById(1L);

        assertTrue(result.isPresent());
        assertEquals("BUY", result.get().getType());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve all transactions successfully")
    void testGetAllTransactions_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve transactions by asset ID successfully")
    void testGetTransactionsByAssetId_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAssetId(1L)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByAssetId(1L);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByAssetId(1L);
    }

    @Test
    @DisplayName("Should retrieve transactions by asset ID ordered by date desc")
    void testGetTransactionsByAssetIdOrderByDateDesc_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAssetIdOrderByTradeDateDesc(1L)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByAssetIdOrderByDateDesc(1L);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByAssetIdOrderByTradeDateDesc(1L);
    }

    @Test
    @DisplayName("Should retrieve transactions by type successfully")
    void testGetTransactionsByType_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByType("BUY")).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByType("BUY");

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByType("BUY");
    }

    @Test
    @DisplayName("Should retrieve transactions by date range successfully")
    void testGetTransactionsByDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findTransactionsByDateRange(startDate, endDate)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByDateRange(startDate, endDate);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findTransactionsByDateRange(startDate, endDate);
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void testGetTransactionsByDateRange_InvalidDates() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(30);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionsByDateRange(startDate, endDate);
        });
        assertEquals("Start date cannot be after end date", exception.getMessage());
    }

    @Test
    @DisplayName("Should retrieve transactions by asset and date range successfully")
    void testGetTransactionsByAssetAndDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAssetIdAndTradeDateBetween(1L, startDate, endDate)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByAssetAndDateRange(1L, startDate, endDate);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByAssetIdAndTradeDateBetween(1L, startDate, endDate);
    }

    @Test
    @DisplayName("Should get transaction count by asset ID successfully")
    void testGetTransactionCountByAssetId_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAssetId(1L)).thenReturn(transactions);

        long result = transactionService.getTransactionCountByAssetId(1L);

        assertEquals(1L, result);
    }

    @Test
    @DisplayName("Should check if transactions exist by asset ID")
    void testTransactionExistsByAssetId_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAssetId(1L)).thenReturn(transactions);

        boolean result = transactionService.transactionExistsByAssetId(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when no transactions exist for asset")
    void testTransactionExistsByAssetId_Empty() {
        when(transactionRepository.findByAssetId(999L)).thenReturn(Arrays.asList());

        boolean result = transactionService.transactionExistsByAssetId(999L);

        assertFalse(result);
    }
}
