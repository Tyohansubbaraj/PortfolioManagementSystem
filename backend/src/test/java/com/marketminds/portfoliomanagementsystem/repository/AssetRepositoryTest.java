package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Asset;
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
class AssetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssetRepository assetRepository;

    private Asset asset1;
    private Asset asset2;
    private Asset asset3;

    @BeforeEach
    void setUp() {
        // Create test assets
        asset1 = new Asset("PLTR", "Palantir Technologies Inc.", "Stock", "Technology", new BigDecimal("150.25"), LocalDateTime.now());
        asset2 = new Asset("MINING", "The Mining Company", "Stock", "Materials", new BigDecimal("320.50"), LocalDateTime.now());
        asset3 = new Asset("RKLB", "Rocket Lab USA Inc.", "Stock", "Aerospace", new BigDecimal("140.75"), LocalDateTime.now());

        // Persist assets to test database
        entityManager.persist(asset1);
        entityManager.persist(asset2);
        entityManager.persist(asset3);
        entityManager.flush();
    }

    @Test
    void testFindBySymbol_Success() {
        // Act
        Optional<Asset> result = assetRepository.findBySymbol("PLTR");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("PLTR", result.get().getSymbol());
        assertEquals("Palantir Technologies Inc.", result.get().getName());
    }

    @Test
    void testFindBySymbol_NotFound() {
        // Act
        Optional<Asset> result = assetRepository.findBySymbol("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByType_Success() {
        // Arrange
        Asset bondAsset = new Asset("BOND1", "Bond Asset", "Bond", "Finance", new BigDecimal("100.00"), LocalDateTime.now());
        entityManager.persist(bondAsset);
        entityManager.flush();

        // Act
        List<Asset> stocks = assetRepository.findByType("Stock");
        List<Asset> bonds = assetRepository.findByType("Bond");

        // Assert
        assertEquals(3, stocks.size());
        assertEquals(1, bonds.size());
        assertTrue(stocks.stream().allMatch(a -> "Stock".equals(a.getType())));
        assertTrue(bonds.stream().allMatch(a -> "Bond".equals(a.getType())));
    }

    @Test
    void testFindByType_NoResults() {
        // Act
        List<Asset> results = assetRepository.findByType("Cryptocurrency");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindBySector_Success() {
        // Act
        List<Asset> techAssets = assetRepository.findBySector("Technology");

        // Assert
        assertEquals(1, techAssets.size());
        assertTrue(techAssets.stream().allMatch(a -> "Technology".equals(a.getSector())));
    }

    @Test
    void testFindBySector_NoResults() {
        // Act
        List<Asset> results = assetRepository.findBySector("Healthcare");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByTypeAndSector_Success() {
        // Act
        List<Asset> results = assetRepository.findByTypeAndSector("Stock", "Technology");

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.stream().allMatch(a -> "Stock".equals(a.getType()) && "Technology".equals(a.getSector())));
    }

    @Test
    void testFindByTypeAndSector_PartialMatch() {
        // Act
        List<Asset> results = assetRepository.findByTypeAndSector("Bond", "Technology");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindAll() {
        // Act
        List<Asset> allAssets = assetRepository.findAll();

        // Assert
        assertEquals(3, allAssets.size());
    }

    @Test
    void testSaveAsset() {
        // Arrange
        Asset newAsset = new Asset("TSLA", "Tesla Inc.", "Stock", "Automotive", new BigDecimal("250.00"), LocalDateTime.now());

        // Act
        Asset savedAsset = assetRepository.save(newAsset);
        entityManager.flush();

        // Assert
        assertNotNull(savedAsset.getId());
        Optional<Asset> retrievedAsset = assetRepository.findBySymbol("TSLA");
        assertTrue(retrievedAsset.isPresent());
        assertEquals("Tesla Inc.", retrievedAsset.get().getName());
    }

    @Test
    void testUpdateAsset() {
        // Arrange
        Optional<Asset> foundAsset = assetRepository.findBySymbol("PLTR");
        assertTrue(foundAsset.isPresent());
        Asset assetToUpdate = foundAsset.get();

        // Act
        assetToUpdate.setCurrentPrice(new BigDecimal("160.00"));
        assetToUpdate.setLastUpdated(LocalDateTime.now());
        assetRepository.save(assetToUpdate);
        entityManager.flush();

        // Assert
        Optional<Asset> updatedAsset = assetRepository.findBySymbol("PLTR");
        assertTrue(updatedAsset.isPresent());
        assertEquals(new BigDecimal("160.00"), updatedAsset.get().getCurrentPrice());
    }

    @Test
    void testDeleteAsset() {
        // Arrange
        Optional<Asset> foundAsset = assetRepository.findBySymbol("MINING");
        assertTrue(foundAsset.isPresent());

        // Act
        assetRepository.delete(foundAsset.get());
        entityManager.flush();

        // Assert
        Optional<Asset> deletedAsset = assetRepository.findBySymbol("MINING");
        assertFalse(deletedAsset.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Optional<Asset> foundAsset = assetRepository.findBySymbol("RKLB");
        assertTrue(foundAsset.isPresent());
        Long assetId = foundAsset.get().getId();

        // Act
        assetRepository.deleteById(assetId);
        entityManager.flush();

        // Assert
        Optional<Asset> deletedAsset = assetRepository.findById(assetId);
        assertFalse(deletedAsset.isPresent());
    }

    @Test
    void testFindById() {
        // Arrange
        Optional<Asset> foundAsset = assetRepository.findBySymbol("PLTR");
        assertTrue(foundAsset.isPresent());
        Long assetId = foundAsset.get().getId();

        // Act
        Optional<Asset> result = assetRepository.findById(assetId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("PLTR", result.get().getSymbol());
    }
}
