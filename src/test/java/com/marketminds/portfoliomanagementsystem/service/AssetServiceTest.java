package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.service.impl.AssetServiceImpl;
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

@DisplayName("Asset Service Unit Tests")
class AssetServiceTest {

    private AssetService assetService;

    @Mock
    private AssetRepository assetRepository;

    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assetService = new AssetServiceImpl(assetRepository);

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
    @DisplayName("Should create a new asset successfully")
    void testCreateAsset_Success() {
        when(assetRepository.findBySymbol("AAPL")).thenReturn(Optional.empty());
        when(assetRepository.save(testAsset)).thenReturn(testAsset);

        Asset result = assetService.createAsset(testAsset);

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals("Apple Inc.", result.getName());
        verify(assetRepository, times(1)).findBySymbol("AAPL");
        verify(assetRepository, times(1)).save(testAsset);
    }

    @Test
    @DisplayName("Should throw exception when creating asset with null")
    void testCreateAsset_NullAsset() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetService.createAsset(null);
        });
        assertEquals("Asset cannot be null", exception.getMessage());
        verify(assetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating asset with null symbol")
    void testCreateAsset_NullSymbol() {
        testAsset.setSymbol(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetService.createAsset(testAsset);
        });
        assertEquals("Asset symbol cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating asset with duplicate symbol")
    void testCreateAsset_DuplicateSymbol() {
        when(assetRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetService.createAsset(testAsset);
        });
        assertEquals("Asset with symbol 'AAPL' already exists", exception.getMessage());
        verify(assetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update an existing asset successfully")
    void testUpdateAsset_Success() {
        when(assetRepository.existsById(1L)).thenReturn(true);
        when(assetRepository.save(testAsset)).thenReturn(testAsset);

        Asset result = assetService.updateAsset(testAsset);

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        verify(assetRepository, times(1)).existsById(1L);
        verify(assetRepository, times(1)).save(testAsset);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent asset")
    void testUpdateAsset_NonExistent() {
        when(assetRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetService.updateAsset(testAsset);
        });
        assertEquals("Asset with ID 1 does not exist", exception.getMessage());
        verify(assetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete an asset successfully")
    void testDeleteAsset_Success() {
        when(assetRepository.existsById(1L)).thenReturn(true);

        assetService.deleteAsset(1L);

        verify(assetRepository, times(1)).existsById(1L);
        verify(assetRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent asset")
    void testDeleteAsset_NonExistent() {
        when(assetRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetService.deleteAsset(999L);
        });
        assertEquals("Asset with ID 999 does not exist", exception.getMessage());
        verify(assetRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should retrieve asset by ID successfully")
    void testGetAssetById_Success() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(testAsset));

        Optional<Asset> result = assetService.getAssetById(1L);

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        verify(assetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional when asset not found")
    void testGetAssetById_NotFound() {
        when(assetRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Asset> result = assetService.getAssetById(999L);

        assertFalse(result.isPresent());
        verify(assetRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should retrieve all assets successfully")
    void testGetAllAssets_Success() {
        Asset asset1 = testAsset;
        Asset asset2 = new Asset();
        asset2.setId(2L);
        asset2.setSymbol("GOOGL");
        asset2.setName("Alphabet Inc.");
        asset2.setType("Stock");
        asset2.setSector("Technology");
        asset2.setCurrentPrice(new BigDecimal("140.75"));
        asset2.setLastUpdated(LocalDateTime.now());

        List<Asset> assets = Arrays.asList(asset1, asset2);
        when(assetRepository.findAll()).thenReturn(assets);

        List<Asset> result = assetService.getAllAssets();

        assertEquals(2, result.size());
        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve assets by symbol successfully")
    void testGetAssetBySymbol_Success() {
        when(assetRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));

        Optional<Asset> result = assetService.getAssetBySymbol("AAPL");

        assertTrue(result.isPresent());
        assertEquals("Apple Inc.", result.get().getName());
        verify(assetRepository, times(1)).findBySymbol("AAPL");
    }

    @Test
    @DisplayName("Should retrieve assets by type successfully")
    void testGetAssetsByType_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetRepository.findByType("Stock")).thenReturn(assets);

        List<Asset> result = assetService.getAssetsByType("Stock");

        assertEquals(1, result.size());
        verify(assetRepository, times(1)).findByType("Stock");
    }

    @Test
    @DisplayName("Should retrieve assets by sector successfully")
    void testGetAssetsBySector_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetRepository.findBySector("Technology")).thenReturn(assets);

        List<Asset> result = assetService.getAssetsBySector("Technology");

        assertEquals(1, result.size());
        verify(assetRepository, times(1)).findBySector("Technology");
    }

    @Test
    @DisplayName("Should retrieve assets by type and sector successfully")
    void testGetAssetsByTypeAndSector_Success() {
        List<Asset> assets = Arrays.asList(testAsset);
        when(assetRepository.findByTypeAndSector("Stock", "Technology")).thenReturn(assets);

        List<Asset> result = assetService.getAssetsByTypeAndSector("Stock", "Technology");

        assertEquals(1, result.size());
        verify(assetRepository, times(1)).findByTypeAndSector("Stock", "Technology");
    }

    @Test
    @DisplayName("Should check if asset exists by symbol")
    void testAssetExistsBySymbol_Success() {
        when(assetRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));

        boolean result = assetService.assetExistsBySymbol("AAPL");

        assertTrue(result);
        verify(assetRepository, times(1)).findBySymbol("AAPL");
    }

    @Test
    @DisplayName("Should return false when asset symbol not found")
    void testAssetExistsBySymbol_NotFound() {
        when(assetRepository.findBySymbol("INVALID")).thenReturn(Optional.empty());

        boolean result = assetService.assetExistsBySymbol("INVALID");

        assertFalse(result);
    }
}
