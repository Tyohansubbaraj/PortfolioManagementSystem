package com.marketminds.portfoliomanagementsystem.controller;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Watchlist;
import com.marketminds.portfoliomanagementsystem.service.WatchlistService;
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

@DisplayName("Watchlist Controller Unit Tests")
class WatchlistControllerTest {

    private WatchlistController watchlistController;

    @Mock
    private WatchlistService watchlistService;

    private Watchlist testWatchlist;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        watchlistController = new WatchlistController(watchlistService);

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
    @DisplayName("Should create watchlist entry and return 201 Created")
    void testCreateWatchlistEntry_Success() {
        when(watchlistService.createWatchlistEntry(testWatchlist)).thenReturn(testWatchlist);

        ResponseEntity<Watchlist> response = watchlistController.createWatchlistEntry(testWatchlist);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testWatchlist, response.getBody());
        verify(watchlistService, times(1)).createWatchlistEntry(testWatchlist);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when create fails")
    void testCreateWatchlistEntry_BadRequest() {
        when(watchlistService.createWatchlistEntry(testWatchlist))
                .thenThrow(new IllegalArgumentException("Asset already in watchlist"));

        ResponseEntity<Watchlist> response = watchlistController.createWatchlistEntry(testWatchlist);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error on unexpected exception")
    void testCreateWatchlistEntry_InternalServerError() {
        when(watchlistService.createWatchlistEntry(testWatchlist))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Watchlist> response = watchlistController.createWatchlistEntry(testWatchlist);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get all watchlist entries and return 200 OK")
    void testGetAllWatchlistEntries_Success() {
        List<Watchlist> watchlists = Arrays.asList(testWatchlist);
        when(watchlistService.getAllWatchlistEntries()).thenReturn(watchlists);

        ResponseEntity<List<Watchlist>> response = watchlistController.getAllWatchlistEntries();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return 204 No Content when no entries found")
    void testGetAllWatchlistEntries_Empty() {
        when(watchlistService.getAllWatchlistEntries()).thenReturn(Arrays.asList());

        ResponseEntity<List<Watchlist>> response = watchlistController.getAllWatchlistEntries();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get watchlist entry by ID and return 200 OK")
    void testGetWatchlistEntryById_Success() {
        when(watchlistService.getWatchlistEntryById(1L)).thenReturn(Optional.of(testWatchlist));

        ResponseEntity<Watchlist> response = watchlistController.getWatchlistEntryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testWatchlist, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 Not Found when entry doesn't exist")
    void testGetWatchlistEntryById_NotFound() {
        when(watchlistService.getWatchlistEntryById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Watchlist> response = watchlistController.getWatchlistEntryById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get watchlist entry by asset ID and return 200 OK")
    void testGetWatchlistEntryByAssetId_Success() {
        when(watchlistService.getWatchlistEntryByAssetId(1L)).thenReturn(Optional.of(testWatchlist));

        ResponseEntity<Watchlist> response = watchlistController.getWatchlistEntryByAssetId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testWatchlist, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when entry for asset not found")
    void testGetWatchlistEntryByAssetId_NotFound() {
        when(watchlistService.getWatchlistEntryByAssetId(999L)).thenReturn(Optional.empty());

        ResponseEntity<Watchlist> response = watchlistController.getWatchlistEntryByAssetId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should check if asset in watchlist and return 200 OK with true")
    void testIsAssetInWatchlist_True() {
        when(watchlistService.isAssetInWatchlist(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = watchlistController.isAssetInWatchlist(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @DisplayName("Should check if asset not in watchlist and return 200 OK with false")
    void testIsAssetInWatchlist_False() {
        when(watchlistService.isAssetInWatchlist(999L)).thenReturn(false);

        ResponseEntity<Boolean> response = watchlistController.isAssetInWatchlist(999L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Should add asset to watchlist and return 201 Created")
    void testAddAssetToWatchlist_Success() {
        when(watchlistService.addAssetToWatchlist(1L, "Monitor this stock"))
                .thenReturn(testWatchlist);

        ResponseEntity<Watchlist> response = watchlistController.addAssetToWatchlist(1L, "Monitor this stock");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testWatchlist, response.getBody());
        verify(watchlistService, times(1)).addAssetToWatchlist(1L, "Monitor this stock");
    }

    @Test
    @DisplayName("Should add asset without notes and return 201 Created")
    void testAddAssetToWatchlist_NoNotes() {
        when(watchlistService.addAssetToWatchlist(1L, null))
                .thenReturn(testWatchlist);

        ResponseEntity<Watchlist> response = watchlistController.addAssetToWatchlist(1L, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when adding fails")
    void testAddAssetToWatchlist_BadRequest() {
        when(watchlistService.addAssetToWatchlist(1L, "notes"))
                .thenThrow(new IllegalArgumentException("Asset not found"));

        ResponseEntity<Watchlist> response = watchlistController.addAssetToWatchlist(1L, "notes");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should remove asset from watchlist and return 204 No Content")
    void testRemoveAssetFromWatchlist_Success() {
        doNothing().when(watchlistService).removeAssetFromWatchlist(1L);

        ResponseEntity<Void> response = watchlistController.removeAssetFromWatchlist(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(watchlistService, times(1)).removeAssetFromWatchlist(1L);
    }

    @Test
    @DisplayName("Should return 404 when asset to remove not in watchlist")
    void testRemoveAssetFromWatchlist_NotFound() {
        doThrow(new IllegalArgumentException("Asset not in watchlist"))
                .when(watchlistService).removeAssetFromWatchlist(999L);

        ResponseEntity<Void> response = watchlistController.removeAssetFromWatchlist(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update watchlist entry and return 200 OK")
    void testUpdateWatchlistEntry_Success() {
        when(watchlistService.updateWatchlistEntry(testWatchlist)).thenReturn(testWatchlist);

        ResponseEntity<Watchlist> response = watchlistController.updateWatchlistEntry(1L, testWatchlist);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testWatchlist, response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when ID doesn't match")
    void testUpdateWatchlistEntry_MismatchedId() {
        testWatchlist.setId(2L);

        ResponseEntity<Watchlist> response = watchlistController.updateWatchlistEntry(1L, testWatchlist);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when update fails")
    void testUpdateWatchlistEntry_BadRequest() {
        testWatchlist.setId(1L);
        when(watchlistService.updateWatchlistEntry(testWatchlist))
                .thenThrow(new IllegalArgumentException("Invalid entry"));

        ResponseEntity<Watchlist> response = watchlistController.updateWatchlistEntry(1L, testWatchlist);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete watchlist entry and return 204 No Content")
    void testDeleteWatchlistEntry_Success() {
        doNothing().when(watchlistService).deleteWatchlistEntry(1L);

        ResponseEntity<Void> response = watchlistController.deleteWatchlistEntry(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(watchlistService, times(1)).deleteWatchlistEntry(1L);
    }

    @Test
    @DisplayName("Should return 404 when entry to delete not found")
    void testDeleteWatchlistEntry_NotFound() {
        doThrow(new IllegalArgumentException("Entry not found"))
                .when(watchlistService).deleteWatchlistEntry(999L);

        ResponseEntity<Void> response = watchlistController.deleteWatchlistEntry(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get watchlist count and return 200 OK")
    void testGetWatchlistCount_Success() {
        when(watchlistService.getWatchlistCount()).thenReturn(5L);

        ResponseEntity<Long> response = watchlistController.getWatchlistCount();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody());
    }

    @Test
    @DisplayName("Should get zero count and return 200 OK")
    void testGetWatchlistCount_Zero() {
        when(watchlistService.getWatchlistCount()).thenReturn(0L);

        ResponseEntity<Long> response = watchlistController.getWatchlistCount();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody());
    }
}