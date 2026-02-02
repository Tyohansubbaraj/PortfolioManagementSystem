package com.marketminds.portfoliomanagementsystem.scheduler;

import com.marketminds.portfoliomanagementsystem.service.AssetPriceUpdater;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class PriceScheduler {

    private final AssetPriceUpdater assetPriceUpdater;

    public PriceScheduler(AssetPriceUpdater assetPriceUpdater) {
        this.assetPriceUpdater = assetPriceUpdater;
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void updatePrices() {
        assetPriceUpdater.updateAllAssetPrices();
    }
}
