package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.service.AssetService;
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

@DisplayName("Asset Controller Unit Tests")
class AssetControllerTest {

    private AssetController assetController;

    @Mock
    private AssetService assetService;

    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assetController = new AssetController(assetService);

        testAsset = new Asset();
        testAsset.setId(1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType("Stock");
        testAsset.setSector("Technology");
        testAsset.setCurrentPrice(new BigDecimal("150.50"));
        testAsset.setLastUpdated(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create asset and return 201 Created")
    void testCreateAsset_Success() {
        when(assetService.createAsset(testAsset)).thenReturn(testAsset);

        ResponseEntity<Asset> response = assetController.createAsset(testAsset);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testAsset, response.getBody());
        verify(assetService, times(1)).createAsset(testAsset);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when create fails")
    void testCreateAsset_BadRequest() {
        when(assetService.createAsset(testAsset)).thenThrow(new IllegalArgumentException("Duplicate symbol"));

        ResponseEntity<Asset> response = assetController.createAsset(testAsset);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error on unexpected exception")
    void testCreateAsset_InternalServerError() {
        when(assetService.createAsset(testAsset)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Asset> response = assetController.createAsset(testAsset);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get all assets and return 200 OK")
    void testGetAllAssets_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetService.getAllAssets()).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.getAllAssets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(assetService, times(1)).getAllAssets();
    }

    @Test
    @DisplayName("Should return 204 No Content when no assets found")
    void testGetAllAssets_Empty() {
        when(assetService.getAllAssets()).thenReturn(Arrays.asList());

        ResponseEntity<List<Asset>> response = assetController.getAllAssets();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get asset by ID and return 200 OK")
    void testGetAssetById_Success() {
        when(assetService.getAssetById(1L)).thenReturn(Optional.of(testAsset));

        ResponseEntity<Asset> response = assetController.getAssetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAsset, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 Not Found when asset doesn't exist")
    void testGetAssetById_NotFound() {
        when(assetService.getAssetById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Asset> response = assetController.getAssetById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should get asset by symbol and return 200 OK")
    void testGetAssetBySymbol_Success() {
        when(assetService.getAssetBySymbol("AAPL")).thenReturn(Optional.of(testAsset));

        ResponseEntity<Asset> response = assetController.getAssetBySymbol("AAPL");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAsset, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when asset symbol not found")
    void testGetAssetBySymbol_NotFound() {
        when(assetService.getAssetBySymbol("INVALID")).thenReturn(Optional.empty());

        ResponseEntity<Asset> response = assetController.getAssetBySymbol("INVALID");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get assets by type and return 200 OK")
    void testGetAssetsByType_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetService.getAssetsByType("Stock")).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.getAssetsByType("Stock");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return 204 when no assets found for type")
    void testGetAssetsByType_Empty() {
        when(assetService.getAssetsByType("Stock")).thenReturn(Arrays.asList());

        ResponseEntity<List<Asset>> response = assetController.getAssetsByType("Stock");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get assets by sector and return 200 OK")
    void testGetAssetsBySector_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetService.getAssetsBySector("Technology")).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.getAssetsBySector("Technology");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should get assets by type and sector and return 200 OK")
    void testGetAssetsByTypeAndSector_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetService.getAssetsByTypeAndSector("Stock", "Technology")).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.getAssetsByTypeAndSector("Stock", "Technology");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should update asset and return 200 OK")
    void testUpdateAsset_Success() {
        when(assetService.updateAsset(testAsset)).thenReturn(testAsset);

        ResponseEntity<Asset> response = assetController.updateAsset(1L, testAsset);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAsset, response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when ID doesn't match")
    void testUpdateAsset_MismatchedId() {
        testAsset.setId(2L);

        ResponseEntity<Asset> response = assetController.updateAsset(1L, testAsset);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete asset and return 204 No Content")
    void testDeleteAsset_Success() {
        doNothing().when(assetService).deleteAsset(1L);

        ResponseEntity<Void> response = assetController.deleteAsset(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(assetService, times(1)).deleteAsset(1L);
    }

    @Test
    @DisplayName("Should return 404 when asset to delete not found")
    void testDeleteAsset_NotFound() {
        doThrow(new IllegalArgumentException("Asset not found")).when(assetService).deleteAsset(999L);

        ResponseEntity<Void> response = assetController.deleteAsset(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should check asset exists by symbol and return 200 OK")
    void testAssetExistsBySymbol_True() {
        when(assetService.assetExistsBySymbol("AAPL")).thenReturn(true);

        ResponseEntity<Boolean> response = assetController.assetExistsBySymbol("AAPL");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @DisplayName("Should return false when asset symbol doesn't exist")
    void testAssetExistsBySymbol_False() {
        when(assetService.assetExistsBySymbol("INVALID")).thenReturn(false);

        ResponseEntity<Boolean> response = assetController.assetExistsBySymbol("INVALID");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }
}