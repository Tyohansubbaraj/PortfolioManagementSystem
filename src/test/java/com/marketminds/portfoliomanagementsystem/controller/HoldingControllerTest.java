package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.service.HoldingService;
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

@DisplayName("Holding Controller Unit Tests")
class HoldingControllerTest {

    private HoldingController holdingController;

    @Mock
    private HoldingService holdingService;

    private Holding testHolding;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        holdingController = new HoldingController(holdingService);

        testAsset = new Asset();
        testAsset.setId(1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType("Stock");
        testAsset.setSector("Technology");
        testAsset.setCurrentPrice(new BigDecimal("150.50"));
        testAsset.setLastUpdated(LocalDateTime.now());

        testHolding = new Holding();
        testHolding.setId(1L);
        testHolding.setAsset(testAsset);
        testHolding.setTotalQuantity(new BigDecimal("100.00"));
        testHolding.setAvgBuyPrice(new BigDecimal("120.00"));
    }

    @Test
    @DisplayName("Should create holding and return 201 Created")
    void testCreateHolding_Success() {
        when(holdingService.createHolding(testHolding)).thenReturn(testHolding);

        ResponseEntity<Holding> response = holdingController.createHolding(testHolding);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testHolding, response.getBody());
        verify(holdingService, times(1)).createHolding(testHolding);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when create fails")
    void testCreateHolding_BadRequest() {
        when(holdingService.createHolding(testHolding)).thenThrow(new IllegalArgumentException("Invalid holding"));

        ResponseEntity<Holding> response = holdingController.createHolding(testHolding);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get all holdings and return 200 OK")
    void testGetAllHoldings_Success() {
        List<Holding> holdings = Arrays.asList(testHolding);
        when(holdingService.getAllHoldings()).thenReturn(holdings);

        ResponseEntity<List<Holding>> response = holdingController.getAllHoldings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return 204 No Content when no holdings found")
    void testGetAllHoldings_Empty() {
        when(holdingService.getAllHoldings()).thenReturn(Arrays.asList());

        ResponseEntity<List<Holding>> response = holdingController.getAllHoldings();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get holding by ID and return 200 OK")
    void testGetHoldingById_Success() {
        when(holdingService.getHoldingById(1L)).thenReturn(Optional.of(testHolding));

        ResponseEntity<Holding> response = holdingController.getHoldingById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testHolding, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 Not Found when holding doesn't exist")
    void testGetHoldingById_NotFound() {
        when(holdingService.getHoldingById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Holding> response = holdingController.getHoldingById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get holding by asset ID and return 200 OK")
    void testGetHoldingByAssetId_Success() {
        when(holdingService.getHoldingByAssetId(1L)).thenReturn(Optional.of(testHolding));

        ResponseEntity<Holding> response = holdingController.getHoldingByAssetId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testHolding, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when holding for asset not found")
    void testGetHoldingByAssetId_NotFound() {
        when(holdingService.getHoldingByAssetId(999L)).thenReturn(Optional.empty());

        ResponseEntity<Holding> response = holdingController.getHoldingByAssetId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should check holding exists and return 200 OK with true")
    void testHoldingExistsByAssetId_True() {
        when(holdingService.holdingExistsByAssetId(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = holdingController.holdingExistsByAssetId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @DisplayName("Should check holding doesn't exist and return 200 OK with false")
    void testHoldingExistsByAssetId_False() {
        when(holdingService.holdingExistsByAssetId(999L)).thenReturn(false);

        ResponseEntity<Boolean> response = holdingController.holdingExistsByAssetId(999L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Should update holding on buy and return 200 OK")
    void testUpdateHoldingOnBuy_Success() {
        Holding updatedHolding = new Holding();
        updatedHolding.setId(1L);
        updatedHolding.setAsset(testAsset);
        updatedHolding.setTotalQuantity(new BigDecimal("150.00"));
        updatedHolding.setAvgBuyPrice(new BigDecimal("116.67"));

        when(holdingService.updateHoldingOnBuy(1L, new BigDecimal("50"), new BigDecimal("150")))
                .thenReturn(updatedHolding);

        ResponseEntity<Holding> response = holdingController.updateHoldingOnBuy(1L, new BigDecimal("50"), new BigDecimal("150"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedHolding, response.getBody());
        verify(holdingService, times(1)).updateHoldingOnBuy(1L, new BigDecimal("50"), new BigDecimal("150"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when buy fails")
    void testUpdateHoldingOnBuy_BadRequest() {
        when(holdingService.updateHoldingOnBuy(1L, new BigDecimal("50"), new BigDecimal("150")))
                .thenThrow(new IllegalArgumentException("Invalid quantity"));

        ResponseEntity<Holding> response = holdingController.updateHoldingOnBuy(1L, new BigDecimal("50"), new BigDecimal("150"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update holding on sell and return 200 OK")
    void testUpdateHoldingOnSell_Success() {
        Holding updatedHolding = new Holding();
        updatedHolding.setId(1L);
        updatedHolding.setAsset(testAsset);
        updatedHolding.setTotalQuantity(new BigDecimal("70.00"));
        updatedHolding.setAvgBuyPrice(new BigDecimal("120.00"));

        when(holdingService.updateHoldingOnSell(1L, new BigDecimal("30")))
                .thenReturn(updatedHolding);

        ResponseEntity<Holding> response = holdingController.updateHoldingOnSell(1L, new BigDecimal("30"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedHolding, response.getBody());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when sell fails")
    void testUpdateHoldingOnSell_BadRequest() {
        when(holdingService.updateHoldingOnSell(1L, new BigDecimal("150")))
                .thenThrow(new IllegalArgumentException("Insufficient quantity"));

        ResponseEntity<Holding> response = holdingController.updateHoldingOnSell(1L, new BigDecimal("150"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update holding and return 200 OK")
    void testUpdateHolding_Success() {
        when(holdingService.updateHolding(testHolding)).thenReturn(testHolding);

        ResponseEntity<Holding> response = holdingController.updateHolding(1L, testHolding);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testHolding, response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when ID doesn't match")
    void testUpdateHolding_MismatchedId() {
        testHolding.setId(2L);

        ResponseEntity<Holding> response = holdingController.updateHolding(1L, testHolding);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete holding and return 204 No Content")
    void testDeleteHolding_Success() {
        doNothing().when(holdingService).deleteHolding(1L);

        ResponseEntity<Void> response = holdingController.deleteHolding(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(holdingService, times(1)).deleteHolding(1L);
    }

    @Test
    @DisplayName("Should return 404 when holding to delete not found")
    void testDeleteHolding_NotFound() {
        doThrow(new IllegalArgumentException("Holding not found")).when(holdingService).deleteHolding(999L);

        ResponseEntity<Void> response = holdingController.deleteHolding(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
