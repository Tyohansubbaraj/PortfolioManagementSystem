package com.marketminds.portfoliomanagementsystem.repository;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Watchlist;
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
class WatchlistRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WatchlistRepository watchlistRepository;

    private Asset asset1;
    private Asset asset2;
    private Asset asset3;
    private Watchlist watchlist1;

    @BeforeEach
    void setUp() {
        // Create and persist assets
        asset1 = new Asset("AAPL", "Apple Inc.", "Stock", "Technology", new BigDecimal("150.25"), LocalDateTime.now());
        asset2 = new Asset("MSFT", "Microsoft Corporation", "Stock", "Technology", new BigDecimal("320.50"), LocalDateTime.now());
        asset3 = new Asset("GOOGL", "Alphabet Inc.", "Stock", "Technology", new BigDecimal("140.75"), LocalDateTime.now());

        entityManager.persist(asset1);
        entityManager.persist(asset2);
        entityManager.persist(asset3);

        // Create and persist watchlist
        watchlist1 = new Watchlist(asset1, "Monitoring Apple stock for entry point");
        entityManager.persist(watchlist1);
        entityManager.flush();
    }

    @Test
    void testFindByAssetId_Success() {
        // Act
        Optional<Watchlist> result = watchlistRepository.findByAssetId(asset1.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(asset1.getId(), result.get().getAsset().getId());
        assertEquals("Monitoring Apple stock for entry point", result.get().getNotes());
    }

    @Test
    void testFindByAssetId_NotFound() {
        // Act
        Optional<Watchlist> result = watchlistRepository.findByAssetId(asset2.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByAssetId_True() {
        // Act
        boolean exists = watchlistRepository.existsByAssetId(asset1.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByAssetId_False() {
        // Act
        boolean exists = watchlistRepository.existsByAssetId(asset2.getId());

        // Assert
        assertFalse(exists);
    }

    @Test
    void testExistsByAssetId_NonexistentAsset() {
        // Act
        boolean exists = watchlistRepository.existsByAssetId(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testSaveWatchlist() {
        // Arrange
        Watchlist newWatchlist = new Watchlist(asset2, "Checking Microsoft growth potential");

        // Act
        Watchlist savedWatchlist = watchlistRepository.save(newWatchlist);
        entityManager.flush();

        // Assert
        assertNotNull(savedWatchlist.getId());
        Optional<Watchlist> retrievedWatchlist = watchlistRepository.findByAssetId(asset2.getId());
        assertTrue(retrievedWatchlist.isPresent());
        assertEquals("Checking Microsoft growth potential", retrievedWatchlist.get().getNotes());
    }

    @Test
    void testUpdateWatchlist() {
        // Arrange
        Optional<Watchlist> foundWatchlist = watchlistRepository.findByAssetId(asset1.getId());
        assertTrue(foundWatchlist.isPresent());
        Watchlist watchlistToUpdate = foundWatchlist.get();

        // Act
        watchlistToUpdate.setNotes("Updated: Waiting for earnings report");
        watchlistRepository.save(watchlistToUpdate);
        entityManager.flush();

        // Assert
        Optional<Watchlist> updatedWatchlist = watchlistRepository.findByAssetId(asset1.getId());
        assertTrue(updatedWatchlist.isPresent());
        assertEquals("Updated: Waiting for earnings report", updatedWatchlist.get().getNotes());
    }

    @Test
    void testDeleteWatchlist() {
        // Arrange
        Optional<Watchlist> foundWatchlist = watchlistRepository.findByAssetId(asset1.getId());
        assertTrue(foundWatchlist.isPresent());

        // Act
        watchlistRepository.delete(foundWatchlist.get());
        entityManager.flush();

        // Assert
        Optional<Watchlist> deletedWatchlist = watchlistRepository.findByAssetId(asset1.getId());
        assertFalse(deletedWatchlist.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Optional<Watchlist> foundWatchlist = watchlistRepository.findByAssetId(asset1.getId());
        assertTrue(foundWatchlist.isPresent());
        Long watchlistId = foundWatchlist.get().getId();

        // Act
        watchlistRepository.deleteById(watchlistId);
        entityManager.flush();

        // Assert
        Optional<Watchlist> deletedWatchlist = watchlistRepository.findById(watchlistId);
        assertFalse(deletedWatchlist.isPresent());
    }

    @Test
    void testFindById() {
        // Arrange
        Long watchlistId = watchlist1.getId();

        // Act
        Optional<Watchlist> result = watchlistRepository.findById(watchlistId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(asset1.getId(), result.get().getAsset().getId());
    }

    @Test
    void testFindAll() {
        // Arrange
        Watchlist watchlist2 = new Watchlist(asset2, "Monitor for P/E ratio");
        Watchlist watchlist3 = new Watchlist(asset3, "Check dividend yields");
        entityManager.persist(watchlist2);
        entityManager.persist(watchlist3);
        entityManager.flush();

        // Act
        var allWatchlists = watchlistRepository.findAll();

        // Assert
        assertEquals(3, allWatchlists.size());
    }

    @Test
    void testMultipleWatchlistsForDifferentAssets() {
        // Arrange
        Watchlist watchlist2 = new Watchlist(asset2, "Watch for dips");
        Watchlist watchlist3 = new Watchlist(asset3, "Long-term hold candidate");
        watchlistRepository.save(watchlist2);
        watchlistRepository.save(watchlist3);
        entityManager.flush();

        // Act
        var allWatchlists = watchlistRepository.findAll();
        long count = watchlistRepository.findAll().stream()
            .filter(w -> w.getAsset() != null)
            .count();

        // Assert
        assertEquals(3, allWatchlists.size());
        assertEquals(3, count);
    }
}
