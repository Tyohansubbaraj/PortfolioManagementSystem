package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.repository.HoldingRepository;
import com.marketminds.portfoliomanagementsystem.service.impl.HoldingServiceImpl;
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

@DisplayName("Holding Service Unit Tests")
class HoldingServiceTest {

    private HoldingService holdingService;

    @Mock
    private HoldingRepository holdingRepository;

    private Holding testHolding;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        holdingService = new HoldingServiceImpl(holdingRepository);

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
    @DisplayName("Should create a new holding successfully")
    void testCreateHolding_Success() {
        when(holdingRepository.findByAssetId(1L)).thenReturn(Optional.empty());
        when(holdingRepository.save(testHolding)).thenReturn(testHolding);

        Holding result = holdingService.createHolding(testHolding);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getTotalQuantity());
        verify(holdingRepository, times(1)).findByAssetId(1L);
        verify(holdingRepository, times(1)).save(testHolding);
    }

    @Test
    @DisplayName("Should throw exception when creating holding with null")
    void testCreateHolding_NullHolding() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.createHolding(null);
        });
        assertEquals("Holding cannot be null", exception.getMessage());
        verify(holdingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating holding with null asset")
    void testCreateHolding_NullAsset() {
        testHolding.setAsset(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.createHolding(testHolding);
        });
        assertEquals("Holding asset cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating holding with invalid quantity")
    void testCreateHolding_InvalidQuantity() {
        testHolding.setTotalQuantity(BigDecimal.ZERO);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.createHolding(testHolding);
        });
        assertEquals("Total quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating holding with invalid average price")
    void testCreateHolding_InvalidPrice() {
        testHolding.setAvgBuyPrice(new BigDecimal("-10.00"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.createHolding(testHolding);
        });
        assertEquals("Average buy price must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate holding")
    void testCreateHolding_DuplicateAsset() {
        when(holdingRepository.findByAssetId(1L)).thenReturn(Optional.of(testHolding));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.createHolding(testHolding);
        });
        assertEquals("Holding already exists for asset ID 1", exception.getMessage());
        verify(holdingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update an existing holding successfully")
    void testUpdateHolding_Success() {
        when(holdingRepository.existsById(1L)).thenReturn(true);
        when(holdingRepository.save(testHolding)).thenReturn(testHolding);

        Holding result = holdingService.updateHolding(testHolding);

        assertNotNull(result);
        verify(holdingRepository, times(1)).existsById(1L);
        verify(holdingRepository, times(1)).save(testHolding);
    }

    @Test
    @DisplayName("Should delete a holding successfully")
    void testDeleteHolding_Success() {
        when(holdingRepository.existsById(1L)).thenReturn(true);

        holdingService.deleteHolding(1L);

        verify(holdingRepository, times(1)).existsById(1L);
        verify(holdingRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should retrieve holding by ID successfully")
    void testGetHoldingById_Success() {
        when(holdingRepository.findById(1L)).thenReturn(Optional.of(testHolding));

        Optional<Holding> result = holdingService.getHoldingById(1L);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("100.00"), result.get().getTotalQuantity());
        verify(holdingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve all holdings successfully")
    void testGetAllHoldings_Success() {
        List<Holding> holdings = Arrays.asList(testHolding);
        when(holdingRepository.findAll()).thenReturn(holdings);

        List<Holding> result = holdingService.getAllHoldings();

        assertEquals(1, result.size());
        verify(holdingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve holding by asset ID successfully")
    void testGetHoldingByAssetId_Success() {
        when(holdingRepository.findByAssetId(1L)).thenReturn(Optional.of(testHolding));

        Optional<Holding> result = holdingService.getHoldingByAssetId(1L);

        assertTrue(result.isPresent());
        verify(holdingRepository, times(1)).findByAssetId(1L);
    }

    @Test
    @DisplayName("Should check if holding exists by asset ID")
    void testHoldingExistsByAssetId_Success() {
        when(holdingRepository.findByAssetId(1L)).thenReturn(Optional.of(testHolding));

        boolean result = holdingService.holdingExistsByAssetId(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should update holding on buy - new average price calculated correctly")
    void testUpdateHoldingOnBuy_Success() {
        Holding existingHolding = new Holding();
        existingHolding.setId(1L);
        existingHolding.setAsset(testAsset);
        existingHolding.setTotalQuantity(new BigDecimal("100.00"));
        existingHolding.setAvgBuyPrice(new BigDecimal("100.00"));

        when(holdingRepository.findById(1L)).thenReturn(Optional.of(existingHolding));
        when(holdingRepository.save(any(Holding.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Buy 50 more at $150
        Holding result = holdingService.updateHoldingOnBuy(1L, new BigDecimal("50.00"), new BigDecimal("150.00"));

        // Total quantity: 100 + 50 = 150
        // Total cost: (100 * 100) + (50 * 150) = 10000 + 7500 = 17500
        // New avg price: 17500 / 150 = 116.6667
        assertEquals(new BigDecimal("150.00"), result.getTotalQuantity());
        assertTrue(result.getAvgBuyPrice().compareTo(new BigDecimal("116.66")) >= 0);
        verify(holdingRepository, times(1)).save(any(Holding.class));
    }

    @Test
    @DisplayName("Should throw exception when buying with invalid quantity")
    void testUpdateHoldingOnBuy_InvalidQuantity() {
        when(holdingRepository.findById(1L)).thenReturn(Optional.of(testHolding));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.updateHoldingOnBuy(1L, BigDecimal.ZERO, new BigDecimal("150.00"));
        });
        assertEquals("New quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should update holding on sell successfully")
    void testUpdateHoldingOnSell_Success() {
        Holding existingHolding = new Holding();
        existingHolding.setId(1L);
        existingHolding.setAsset(testAsset);
        existingHolding.setTotalQuantity(new BigDecimal("100.00"));
        existingHolding.setAvgBuyPrice(new BigDecimal("100.00"));

        when(holdingRepository.findById(1L)).thenReturn(Optional.of(existingHolding));
        when(holdingRepository.save(any(Holding.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Sell 30 shares
        Holding result = holdingService.updateHoldingOnSell(1L, new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), result.getTotalQuantity());
        assertEquals(new BigDecimal("100.00"), result.getAvgBuyPrice()); // Avg price doesn't change on sell
        verify(holdingRepository, times(1)).save(any(Holding.class));
    }

    @Test
    @DisplayName("Should throw exception when selling more than available quantity")
    void testUpdateHoldingOnSell_ExcessiveQuantity() {
        when(holdingRepository.findById(1L)).thenReturn(Optional.of(testHolding));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.updateHoldingOnSell(1L, new BigDecimal("150.00"));
        });
        assertTrue(exception.getMessage().contains("Cannot sell more quantity than available"));
    }
}
