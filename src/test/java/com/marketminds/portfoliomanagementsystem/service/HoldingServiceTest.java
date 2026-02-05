package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.dto.HoldingDetailsDTO;
import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.repository.HoldingRepository;
import com.marketminds.portfoliomanagementsystem.service.impl.HoldingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    // ---------------------------------------------------
    // CREATE HOLDING
    // ---------------------------------------------------

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
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.createHolding(null)
        );
        assertEquals("Holding cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating holding with null asset")
    void testCreateHolding_NullAsset() {
        testHolding.setAsset(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.createHolding(testHolding)
        );
        assertEquals("Holding asset cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate holding")
    void testCreateHolding_DuplicateAsset() {
        when(holdingRepository.findByAssetId(1L))
                .thenReturn(Optional.of(testHolding));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.createHolding(testHolding)
        );

        assertEquals("Holding already exists for asset ID 1", ex.getMessage());
        verify(holdingRepository, never()).save(any());
    }

    // ---------------------------------------------------
    // GET HOLDING BY ID
    // ---------------------------------------------------

    @Test
    @DisplayName("Should retrieve holding by ID successfully")
    void testGetHoldingById_Success() {
        when(holdingRepository.findById(1L))
                .thenReturn(Optional.of(testHolding));

        HoldingDetailsDTO result = holdingService.getHoldingById(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getQuantity());
        assertEquals("AAPL", result.getAssetSymbol());
        verify(holdingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when holding not found by ID")
    void testGetHoldingById_NotFound() {
        when(holdingRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.getHoldingById(999L)
        );

        assertEquals("Holding with ID 999 not found", ex.getMessage());
    }

    // ---------------------------------------------------
    // GET ALL HOLDINGS
    // ---------------------------------------------------

    @Test
    @DisplayName("Should retrieve all holdings successfully")
    void testGetAllHoldings_Success() {
        when(holdingRepository.findAll())
                .thenReturn(Arrays.asList(testHolding));

        List<Holding> result = holdingService.getAllHoldings();

        assertEquals(1, result.size());
        verify(holdingRepository, times(1)).findAll();
    }

    // ---------------------------------------------------
    // UPDATE HOLDING ON BUY
    // ---------------------------------------------------

    @Test
    @DisplayName("Should update holding on buy successfully")
    void testUpdateHoldingOnBuy_Success() {
        when(holdingRepository.findById(1L))
                .thenReturn(Optional.of(testHolding));

        when(holdingRepository.save(any(Holding.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Holding result = holdingService.updateHoldingOnBuy(
                1L,
                new BigDecimal("50.00"),
                new BigDecimal("150.00")
        );

        assertEquals(new BigDecimal("150.00"), result.getTotalQuantity());
        verify(holdingRepository, times(1)).save(any(Holding.class));
    }

    // ---------------------------------------------------
    // UPDATE HOLDING ON SELL
    // ---------------------------------------------------

    @Test
    @DisplayName("Should update holding on sell successfully")
    void testUpdateHoldingOnSell_Success() {
        when(holdingRepository.findById(1L))
                .thenReturn(Optional.of(testHolding));

        when(holdingRepository.save(any(Holding.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Holding result = holdingService.updateHoldingOnSell(
                1L,
                new BigDecimal("30.00")
        );

        assertEquals(new BigDecimal("70.00"), result.getTotalQuantity());
        verify(holdingRepository, times(1)).save(any(Holding.class));
    }
}
