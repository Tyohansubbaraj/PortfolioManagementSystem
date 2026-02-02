package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Transaction Controller Unit Tests")
class TransactionControllerTest {

    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    private Transaction testTransaction;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);

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
    @DisplayName("Should create transaction and return 201 Created")
    void testCreateTransaction_Success() {
        when(transactionService.createTransaction(testTransaction)).thenReturn(testTransaction);

        ResponseEntity<Transaction> response = transactionController.createTransaction(testTransaction);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTransaction, response.getBody());
        verify(transactionService, times(1)).createTransaction(testTransaction);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when create fails")
    void testCreateTransaction_BadRequest() {
        when(transactionService.createTransaction(testTransaction))
                .thenThrow(new IllegalArgumentException("Invalid transaction"));

        ResponseEntity<Transaction> response = transactionController.createTransaction(testTransaction);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error on unexpected exception")
    void testCreateTransaction_InternalServerError() {
        when(transactionService.createTransaction(testTransaction))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Transaction> response = transactionController.createTransaction(testTransaction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get all transactions and return 200 OK")
    void testGetAllTransactions_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return 204 No Content when no transactions found")
    void testGetAllTransactions_Empty() {
        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList());

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get transaction by ID and return 200 OK")
    void testGetTransactionById_Success() {
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(testTransaction));

        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTransaction, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 Not Found when transaction doesn't exist")
    void testGetTransactionById_NotFound() {
        when(transactionService.getTransactionById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Transaction> response = transactionController.getTransactionById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get transactions by asset ID and return 200 OK")
    void testGetTransactionsByAssetId_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByAssetId(1L)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAssetId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return 204 when no transactions found for asset")
    void testGetTransactionsByAssetId_Empty() {
        when(transactionService.getTransactionsByAssetId(1L)).thenReturn(Arrays.asList());

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAssetId(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get transactions sorted by date and return 200 OK")
    void testGetTransactionsByAssetIdOrderByDateDesc_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByAssetIdOrderByDateDesc(1L)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAssetIdOrderByDateDesc(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should get transactions by type and return 200 OK")
    void testGetTransactionsByType_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByType("BUY")).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByType("BUY");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should get transactions by date range and return 200 OK")
    void testGetTransactionsByDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<Transaction> transactions = Arrays.asList(testTransaction);

        when(transactionService.getTransactionsByDateRange(startDate, endDate)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByDateRange(
                startDate.toString(), endDate.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid date format")
    void testGetTransactionsByDateRange_BadRequest() {
        // Test with invalid date format - should throw DateTimeParseException
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByDateRange(
                "invalid-date", "invalid-date");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when service throws IllegalArgumentException")
    void testGetTransactionsByDateRange_ServiceError() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        when(transactionService.getTransactionsByDateRange(startDate, endDate))
                .thenThrow(new IllegalArgumentException("Invalid date range"));

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByDateRange(
                startDate.toString(), endDate.toString());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get transactions by asset and date range and return 200 OK")
    void testGetTransactionsByAssetAndDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<Transaction> transactions = Arrays.asList(testTransaction);

        when(transactionService.getTransactionsByAssetAndDateRange(1L, startDate, endDate))
                .thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAssetAndDateRange(
                1L, startDate.toString(), endDate.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should get transaction count by asset ID and return 200 OK")
    void testGetTransactionCountByAssetId_Success() {
        when(transactionService.getTransactionCountByAssetId(1L)).thenReturn(5L);

        ResponseEntity<Long> response = transactionController.getTransactionCountByAssetId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody());
    }

    @Test
    @DisplayName("Should check transactions exist by asset ID and return 200 OK with true")
    void testTransactionExistsByAssetId_True() {
        when(transactionService.transactionExistsByAssetId(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = transactionController.transactionExistsByAssetId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @DisplayName("Should check transactions don't exist and return 200 OK with false")
    void testTransactionExistsByAssetId_False() {
        when(transactionService.transactionExistsByAssetId(999L)).thenReturn(false);

        ResponseEntity<Boolean> response = transactionController.transactionExistsByAssetId(999L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Should update transaction and return 200 OK")
    void testUpdateTransaction_Success() {
        when(transactionService.updateTransaction(testTransaction)).thenReturn(testTransaction);

        ResponseEntity<Transaction> response = transactionController.updateTransaction(1L, testTransaction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTransaction, response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when ID doesn't match")
    void testUpdateTransaction_MismatchedId() {
        testTransaction.setId(2L);

        ResponseEntity<Transaction> response = transactionController.updateTransaction(1L, testTransaction);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete transaction and return 204 No Content")
    void testDeleteTransaction_Success() {
        doNothing().when(transactionService).deleteTransaction(1L);

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService, times(1)).deleteTransaction(1L);
    }

    @Test
    @DisplayName("Should return 404 when transaction to delete not found")
    void testDeleteTransaction_NotFound() {
        doThrow(new IllegalArgumentException("Transaction not found"))
                .when(transactionService).deleteTransaction(999L);

        ResponseEntity<Void> response = transactionController.deleteTransaction(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}