package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class HoldingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HoldingRepository holdingRepository;

    private Asset asset1;
    private Asset asset2;
    private Holding holding1;

    @BeforeEach
    void setUp() {
        // Create and persist assets
        asset1 = new Asset("AAPL", "Apple Inc.", "Stock", "Technology", new BigDecimal("150.25"), LocalDateTime.now());
        asset2 = new Asset("MSFT", "Microsoft Corporation", "Stock", "Technology", new BigDecimal("320.50"), LocalDateTime.now());

        entityManager.persist(asset1);
        entityManager.persist(asset2);

        // Create and persist holding
        holding1 = new Holding(asset1, new BigDecimal("100.0000"), new BigDecimal("145.50"));
        entityManager.persist(holding1);
        entityManager.flush();
    }

    @Test
    void testFindByAssetId_Success() {
        // Act
        Optional<Holding> result = holdingRepository.findByAssetId(asset1.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(asset1.getId(), result.get().getAsset().getId());
        assertEquals(new BigDecimal("100.0000"), result.get().getTotalQuantity());
    }

    @Test
    void testFindByAssetId_NotFound() {
        // Act
        Optional<Holding> result = holdingRepository.findByAssetId(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveHolding() {
        // Arrange
        Holding newHolding = new Holding(asset2, new BigDecimal("50.5000"), new BigDecimal("310.00"));

        // Act
        Holding savedHolding = holdingRepository.save(newHolding);
        entityManager.flush();

        // Assert
        assertNotNull(savedHolding.getId());
        Optional<Holding> retrievedHolding = holdingRepository.findByAssetId(asset2.getId());
        assertTrue(retrievedHolding.isPresent());
        assertEquals(new BigDecimal("50.5000"), retrievedHolding.get().getTotalQuantity());
    }

    @Test
    void testUpdateHolding() {
        // Arrange
        Optional<Holding> foundHolding = holdingRepository.findByAssetId(asset1.getId());
        assertTrue(foundHolding.isPresent());
        Holding holdingToUpdate = foundHolding.get();

        // Act
        holdingToUpdate.setTotalQuantity(new BigDecimal("150.0000"));
        holdingToUpdate.setAvgBuyPrice(new BigDecimal("148.00"));
        holdingRepository.save(holdingToUpdate);
        entityManager.flush();

        // Assert
        Optional<Holding> updatedHolding = holdingRepository.findByAssetId(asset1.getId());
        assertTrue(updatedHolding.isPresent());
        assertEquals(new BigDecimal("150.0000"), updatedHolding.get().getTotalQuantity());
        assertEquals(new BigDecimal("148.00"), updatedHolding.get().getAvgBuyPrice());
    }

    @Test
    void testDeleteHolding() {
        // Arrange
        Optional<Holding> foundHolding = holdingRepository.findByAssetId(asset1.getId());
        assertTrue(foundHolding.isPresent());

        // Act
        holdingRepository.delete(foundHolding.get());
        entityManager.flush();

        // Assert
        Optional<Holding> deletedHolding = holdingRepository.findByAssetId(asset1.getId());
        assertFalse(deletedHolding.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Optional<Holding> foundHolding = holdingRepository.findByAssetId(asset1.getId());
        assertTrue(foundHolding.isPresent());
        Long holdingId = foundHolding.get().getId();

        // Act
        holdingRepository.deleteById(holdingId);
        entityManager.flush();

        // Assert
        Optional<Holding> deletedHolding = holdingRepository.findById(holdingId);
        assertFalse(deletedHolding.isPresent());
    }

    @Test
    void testFindById() {
        // Arrange
        Long holdingId = holding1.getId();

        // Act
        Optional<Holding> result = holdingRepository.findById(holdingId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(asset1.getId(), result.get().getAsset().getId());
    }

    @Test
    void testFindAll() {
        // Arrange
        Holding holding2 = new Holding(asset2, new BigDecimal("75.0000"), new BigDecimal("315.00"));
        entityManager.persist(holding2);
        entityManager.flush();

        // Act
        var allHoldings = holdingRepository.findAll();

        // Assert
        assertEquals(2, allHoldings.size());
    }
}
