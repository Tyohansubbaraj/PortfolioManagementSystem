package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Watchlist;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.repository.WatchlistRepository;
import com.marketminds.portfoliomanagementsystem.service.impl.WatchlistServiceImpl;
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

@DisplayName("Watchlist Service Unit Tests")
class WatchlistServiceTest {

    private WatchlistService watchlistService;

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private AssetRepository assetRepository;

    private Watchlist testWatchlist;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        watchlistService = new WatchlistServiceImpl(watchlistRepository, assetRepository);

        testAsset = new Asset();
        testAsset.setId(1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType("Stock");
        testAsset.setSector("Technology");
        testAsset.setCurrentPrice(new BigDecimal("150.50"));
        testAsset.setLastUpdated(LocalDateTime.now());

        testWatchlist = new Watchlist();
        testWatchlist.setId(1L);
        testWatchlist.setAsset(testAsset);
        testWatchlist.setNotes("Good tech stock to monitor");
    }

    @Test
    @DisplayName("Should create a new watchlist entry successfully")
    void testCreateWatchlistEntry_Success() {
        when(watchlistRepository.findByAssetId(1L)).thenReturn(Optional.empty());
        when(watchlistRepository.save(testWatchlist)).thenReturn(testWatchlist);

        Watchlist result = watchlistService.createWatchlistEntry(testWatchlist);

        assertNotNull(result);
        assertEquals("Good tech stock to monitor", result.getNotes());
        verify(watchlistRepository, times(1)).findByAssetId(1L);
        verify(watchlistRepository, times(1)).save(testWatchlist);
    }

    @Test
    @DisplayName("Should throw exception when creating watchlist entry with null")
    void testCreateWatchlistEntry_NullWatchlist() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.createWatchlistEntry(null);
        });
        assertEquals("Watchlist entry cannot be null", exception.getMessage());
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating watchlist entry with null asset")
    void testCreateWatchlistEntry_NullAsset() {
        testWatchlist.setAsset(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.createWatchlistEntry(testWatchlist);
        });
        assertEquals("Watchlist asset cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate watchlist entry")
    void testCreateWatchlistEntry_DuplicateAsset() {
        when(watchlistRepository.findByAssetId(1L)).thenReturn(Optional.of(testWatchlist));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.createWatchlistEntry(testWatchlist);
        });
        assertEquals("Asset is already in the watchlist", exception.getMessage());
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update an existing watchlist entry successfully")
    void testUpdateWatchlistEntry_Success() {
        when(watchlistRepository.existsById(1L)).thenReturn(true);
        when(watchlistRepository.save(testWatchlist)).thenReturn(testWatchlist);

        Watchlist result = watchlistService.updateWatchlistEntry(testWatchlist);

        assertNotNull(result);
        verify(watchlistRepository, times(1)).existsById(1L);
        verify(watchlistRepository, times(1)).save(testWatchlist);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent watchlist entry")
    void testUpdateWatchlistEntry_NonExistent() {
        when(watchlistRepository.existsById(999L)).thenReturn(false);
        testWatchlist.setId(999L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.updateWatchlistEntry(testWatchlist);
        });
        assertEquals("Watchlist entry with ID 999 does not exist", exception.getMessage());
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete a watchlist entry successfully")
    void testDeleteWatchlistEntry_Success() {
        when(watchlistRepository.existsById(1L)).thenReturn(true);

        watchlistService.deleteWatchlistEntry(1L);

        verify(watchlistRepository, times(1)).existsById(1L);
        verify(watchlistRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent watchlist entry")
    void testDeleteWatchlistEntry_NonExistent() {
        when(watchlistRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.deleteWatchlistEntry(999L);
        });
        assertEquals("Watchlist entry with ID 999 does not exist", exception.getMessage());
        verify(watchlistRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should retrieve watchlist entry by ID successfully")
    void testGetWatchlistEntryById_Success() {
        when(watchlistRepository.findById(1L)).thenReturn(Optional.of(testWatchlist));

        Optional<Watchlist> result = watchlistService.getWatchlistEntryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Good tech stock to monitor", result.get().getNotes());
        verify(watchlistRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve all watchlist entries successfully")
    void testGetAllWatchlistEntries_Success() {
        List<Watchlist> watchlists = Arrays.asList(testWatchlist);
        when(watchlistRepository.findAll()).thenReturn(watchlists);

        List<Watchlist> result = watchlistService.getAllWatchlistEntries();

        assertEquals(1, result.size());
        verify(watchlistRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve watchlist entry by asset ID successfully")
    void testGetWatchlistEntryByAssetId_Success() {
        when(watchlistRepository.findByAssetId(1L)).thenReturn(Optional.of(testWatchlist));

        Optional<Watchlist> result = watchlistService.getWatchlistEntryByAssetId(1L);

        assertTrue(result.isPresent());
        verify(watchlistRepository, times(1)).findByAssetId(1L);
    }

    @Test
    @DisplayName("Should check if asset is in watchlist")
    void testIsAssetInWatchlist_Success() {
        when(watchlistRepository.existsByAssetId(1L)).thenReturn(true);

        boolean result = watchlistService.isAssetInWatchlist(1L);

        assertTrue(result);
        verify(watchlistRepository, times(1)).existsByAssetId(1L);
    }

    @Test
    @DisplayName("Should return false when asset is not in watchlist")
    void testIsAssetInWatchlist_NotInWatchlist() {
        when(watchlistRepository.existsByAssetId(999L)).thenReturn(false);

        boolean result = watchlistService.isAssetInWatchlist(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should remove asset from watchlist successfully")
    void testRemoveAssetFromWatchlist_Success() {
        when(watchlistRepository.findByAssetId(1L)).thenReturn(Optional.of(testWatchlist));

        watchlistService.removeAssetFromWatchlist(1L);

        verify(watchlistRepository, times(1)).findByAssetId(1L);
        verify(watchlistRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when removing non-existent asset from watchlist")
    void testRemoveAssetFromWatchlist_NotInWatchlist() {
        when(watchlistRepository.findByAssetId(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.removeAssetFromWatchlist(999L);
        });
        assertEquals("Asset with ID 999 is not in the watchlist", exception.getMessage());
        verify(watchlistRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should add asset to watchlist successfully")
    void testAddAssetToWatchlist_Success() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(testAsset));
        when(watchlistRepository.existsByAssetId(1L)).thenReturn(false);
        when(watchlistRepository.save(any(Watchlist.class))).thenReturn(testWatchlist);

        Watchlist result = watchlistService.addAssetToWatchlist(1L, "Promising tech company");

        assertNotNull(result);
        verify(assetRepository, times(1)).findById(1L);
        verify(watchlistRepository, times(1)).existsByAssetId(1L);
        verify(watchlistRepository, times(1)).save(any(Watchlist.class));
    }

    @Test
    @DisplayName("Should throw exception when adding non-existent asset to watchlist")
    void testAddAssetToWatchlist_AssetNotFound() {
        when(assetRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.addAssetToWatchlist(999L, "Some notes");
        });
        assertEquals("Asset with ID 999 does not exist", exception.getMessage());
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate asset to watchlist")
    void testAddAssetToWatchlist_AlreadyInWatchlist() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(testAsset));
        when(watchlistRepository.existsByAssetId(1L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            watchlistService.addAssetToWatchlist(1L, "Some notes");
        });
        assertEquals("Asset is already in the watchlist", exception.getMessage());
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get watchlist count successfully")
    void testGetWatchlistCount_Success() {
        List<Watchlist> watchlists = Arrays.asList(testWatchlist);
        when(watchlistRepository.findAll()).thenReturn(watchlists);

        long result = watchlistService.getWatchlistCount();

        assertEquals(1L, result);
    }
}
