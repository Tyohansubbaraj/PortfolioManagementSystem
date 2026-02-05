package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AssetPriceUpdater {

    private final AssetRepository assetRepository;
    private final PriceService priceService;

    public AssetPriceUpdater(AssetRepository assetRepository,
                             PriceService priceService) {
        this.assetRepository = assetRepository;
        this.priceService = priceService;
    }

    @Transactional
    public void updateAllAssetPrices() {

        List<Asset> assets = assetRepository.findAll();
        if (assets.isEmpty()) return;

        List<String> symbols = assets.stream()
                .map(Asset::getSymbol)
                .toList();

        Map<String, BigDecimal> prices =
                priceService.fetchCurrentPrices(symbols);

        LocalDateTime now = LocalDateTime.now();

        assets.forEach(asset -> {
            BigDecimal price = prices.get(asset.getSymbol());
            if (price != null) {
                asset.setCurrentPrice(price);
                asset.setLastUpdated(now);
            }
        });

        assetRepository.saveAll(assets);
    }
}
