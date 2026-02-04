package com.marketminds.portfoliomanagementsystem.service.impl;

import com.marketminds.portfoliomanagementsystem.dto.HoldingDetailsDTO;
import com.marketminds.portfoliomanagementsystem.dto.HoldingsSummaryDTO;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.repository.HoldingRepository;
import com.marketminds.portfoliomanagementsystem.service.HoldingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HoldingServiceImpl implements HoldingService {

    private final HoldingRepository holdingRepository;

    public HoldingServiceImpl(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @Override
    public Holding createHolding(Holding holding) {
        if (holding == null) {
            throw new IllegalArgumentException("Holding cannot be null");
        }
        if (holding.getAsset() == null) {
            throw new IllegalArgumentException("Holding asset cannot be null");
        }
        if (holding.getTotalQuantity() == null || holding.getTotalQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total quantity must be greater than zero");
        }
        if (holding.getAvgBuyPrice() == null || holding.getAvgBuyPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Average buy price must be greater than zero");
        }
        if (holdingRepository.findByAssetId(holding.getAsset().getId()).isPresent()) {
            throw new IllegalArgumentException("Holding already exists for asset ID " + holding.getAsset().getId());
        }
        return holdingRepository.save(holding);
    }

    @Override
    public Holding updateHolding(Holding holding) {
        if (holding == null || holding.getId() == null) {
            throw new IllegalArgumentException("Holding and Holding ID cannot be null");
        }
        if (!holdingRepository.existsById(holding.getId())) {
            throw new IllegalArgumentException("Holding with ID " + holding.getId() + " does not exist");
        }
        return holdingRepository.save(holding);
    }

    @Override
    public void deleteHolding(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Holding ID cannot be null");
        }
        if (!holdingRepository.existsById(id)) {
            throw new IllegalArgumentException("Holding with ID " + id + " does not exist");
        }
        holdingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public HoldingDetailsDTO getHoldingById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Holding ID cannot be null");
        }

        Holding holding = holdingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Holding with ID " + id + " not found")
                );

        BigDecimal quantity = holding.getTotalQuantity();
        BigDecimal avgBuyPrice = holding.getAvgBuyPrice();
        BigDecimal currentPrice = holding.getAsset().getCurrentPrice();

        BigDecimal totalInvested = quantity.multiply(avgBuyPrice);
        BigDecimal currentValue = quantity.multiply(currentPrice);
        BigDecimal profitLoss = currentValue.subtract(totalInvested);

        BigDecimal profitLossPercent = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            profitLossPercent = profitLoss
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        HoldingDetailsDTO dto = new HoldingDetailsDTO();
        dto.setHoldingId(holding.getId());
        dto.setAssetName(holding.getAsset().getName());
        dto.setAssetSymbol(holding.getAsset().getSymbol());

        dto.setQuantity(quantity);
        dto.setAvgBuyPrice(avgBuyPrice);
        dto.setCurrentPrice(currentPrice);
        dto.setBuyDate(holding.getBuyDate());

        dto.setTotalInvested(totalInvested);
        dto.setCurrentValue(currentValue);
        dto.setProfitLoss(profitLoss);
        dto.setProfitLossPercent(profitLossPercent);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Holding> getAllHoldings() {
        return holdingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Holding> getHoldingByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return holdingRepository.findByAssetId(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean holdingExistsByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return holdingRepository.findByAssetId(assetId).isPresent();
    }

    @Override
    public Holding updateHoldingOnBuy(Long holdingId, BigDecimal newQuantity, BigDecimal newPrice) {
        if (holdingId == null) {
            throw new IllegalArgumentException("Holding ID cannot be null");
        }
        if (newQuantity == null || newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("New quantity must be greater than zero");
        }
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("New price must be greater than zero");
        }

        Optional<Holding> holdingOptional = holdingRepository.findById(holdingId);
        if (!holdingOptional.isPresent()) {
            throw new IllegalArgumentException("Holding with ID " + holdingId + " does not exist");
        }

        Holding holding = holdingOptional.get();
        BigDecimal currentQuantity = holding.getTotalQuantity();
        BigDecimal currentAvgPrice = holding.getAvgBuyPrice();

        // Calculate new average buy price: (current_qty * current_avg_price + new_qty * new_price) / (current_qty + new_qty)
        BigDecimal totalCost = currentQuantity.multiply(currentAvgPrice).add(newQuantity.multiply(newPrice));
        BigDecimal totalQuantity = currentQuantity.add(newQuantity);
        BigDecimal newAvgPrice = totalCost.divide(totalQuantity, 4, java.math.RoundingMode.HALF_UP);

        holding.setTotalQuantity(totalQuantity);
        holding.setAvgBuyPrice(newAvgPrice);

        return holdingRepository.save(holding);
    }

    @Override
    public Holding updateHoldingOnSell(Long holdingId, BigDecimal quantityToSell) {
        if (holdingId == null) {
            throw new IllegalArgumentException("Holding ID cannot be null");
        }
        if (quantityToSell == null || quantityToSell.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity to sell must be greater than zero");
        }

        Optional<Holding> holdingOptional = holdingRepository.findById(holdingId);
        if (!holdingOptional.isPresent()) {
            throw new IllegalArgumentException("Holding with ID " + holdingId + " does not exist");
        }

        Holding holding = holdingOptional.get();
        BigDecimal currentQuantity = holding.getTotalQuantity();

        if (quantityToSell.compareTo(currentQuantity) > 0) {
            throw new IllegalArgumentException("Cannot sell more quantity than available. Available: " + currentQuantity + ", Trying to sell: " + quantityToSell);
        }

        BigDecimal newQuantity = currentQuantity.subtract(quantityToSell);
        holding.setTotalQuantity(newQuantity);

        return holdingRepository.save(holding);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public HoldingsSummaryDTO getHoldingsSummary() {
//        List<Holding> holdings = holdingRepository.findAll();
//
//        BigDecimal totalInvested = BigDecimal.ZERO;
//        BigDecimal currentValue = BigDecimal.ZERO;
//
//        for (Holding holding : holdings) {
//
//            BigDecimal quantity = holding.getTotalQuantity();
//            BigDecimal avgBuyPrice = holding.getAvgBuyPrice();
//            BigDecimal currentPrice = holding.getAsset().getCurrentPrice();
//
//            totalInvested = totalInvested.add(quantity.multiply(avgBuyPrice));
//            currentValue = currentValue.add(quantity.multiply(currentPrice));
//
//        }
//
//        BigDecimal totalProfitLoss = currentValue.subtract(totalInvested);
//        BigDecimal totalProfitLossPercentage = BigDecimal.ZERO;;
//        BigDecimal totalProfitLossPercent = BigDecimal.ZERO;
//        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
//            totalProfitLossPercent = totalProfitLoss
//                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
//                    .multiply(BigDecimal.valueOf(100));
//        }
//
//        HoldingsSummaryDTO summary = new HoldingsSummaryDTO();
//        summary.setTotalInvested(totalInvested);
//        summary.setCurrentValue(currentValue);
//        summary.setTotalProfitLoss(totalProfitLoss);
//        summary.setTotalProfitLossPercent(totalProfitLossPercent);
//
//        return summary;
//    }
}
