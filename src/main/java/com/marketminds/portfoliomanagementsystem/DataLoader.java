package com.marketminds.portfoliomanagementsystem;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.model.Holding;
import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.repository.HoldingRepository;
import com.marketminds.portfoliomanagementsystem.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final AssetRepository assetRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    public DataLoader(AssetRepository assetRepository, HoldingRepository holdingRepository, TransactionRepository transactionRepository) {
        this.assetRepository = assetRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed when no assets present
        if (assetRepository.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();

        Asset aapl = new Asset("AAPL", "Apple Inc.", "Stock", "Technology", new BigDecimal("170.23"), now);
        Asset msft = new Asset("MSFT", "Microsoft Corp.", "Stock", "Technology", new BigDecimal("320.10"), now);
        Asset goog = new Asset("GOOG", "Alphabet Inc.", "Stock", "Technology", new BigDecimal("125.75"), now);

        assetRepository.saveAll(List.of(aapl, msft, goog));

        Holding haapl = new Holding(aapl, new BigDecimal("10"), new BigDecimal("150.00"));
        Holding hmsft = new Holding(msft, new BigDecimal("5"), new BigDecimal("250.00"));

        holdingRepository.saveAll(List.of(haapl, hmsft));

        Transaction t1 = new Transaction(aapl, "BUY", new BigDecimal("10"), new BigDecimal("150.00"), now.minusDays(30));
        Transaction t2 = new Transaction(msft, "BUY", new BigDecimal("5"), new BigDecimal("250.00"), now.minusDays(20));
        Transaction t3 = new Transaction(aapl, "BUY", new BigDecimal("2"), new BigDecimal("165.00"), now.minusDays(5));

        transactionRepository.saveAll(List.of(t1, t2, t3));

        System.out.println("Sample data inserted: assets=" + assetRepository.count() + ", holdings=" + holdingRepository.count() + ", transactions=" + transactionRepository.count());
    }
}
