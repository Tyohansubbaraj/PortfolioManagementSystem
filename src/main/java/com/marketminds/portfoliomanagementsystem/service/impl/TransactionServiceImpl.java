package com.marketminds.portfoliomanagementsystem.service.impl;

import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.repository.TransactionRepository;
import com.marketminds.portfoliomanagementsystem.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getAsset() == null) {
            throw new IllegalArgumentException("Transaction asset cannot be null");
        }
        if (transaction.getType() == null || transaction.getType().isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }
        if (!transaction.getType().equalsIgnoreCase("BUY") && !transaction.getType().equalsIgnoreCase("SELL")) {
            throw new IllegalArgumentException("Transaction type must be either BUY or SELL");
        }
        if (transaction.getQuantity() == null || transaction.getQuantity().signum() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (transaction.getPrice() == null || transaction.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        log.info("Creating new transaction for asset ID: {}", transaction.getAsset().getId());
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        if (transaction == null || transaction.getId() == null) {
            throw new IllegalArgumentException("Transaction and Transaction ID cannot be null");
        }
        if (!transactionRepository.existsById(transaction.getId())) {
            throw new IllegalArgumentException("Transaction with ID " + transaction.getId() + " does not exist");
        }
        log.info("Updating transaction with ID: {}", transaction.getId());
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction with ID " + id + " does not exist");
        }
        log.info("Deleting transaction with ID: {}",id);
        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        log.info("Fetching transaction with ID: {}", id);
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions");
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        log.info("Fetching all transactions for asset ID: {}",assetId);
        return transactionRepository.findByAssetId(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAssetIdOrderByDateDesc(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        log.info("Fetching all transactions for asset ID: {} ordered by trade date descending", assetId);
        return transactionRepository.findByAssetIdOrderByTradeDateDesc(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        log.info("Fetching all transactions of type: {}", type);
        return transactionRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        log.info("Fetching all transactions between {} and {}", startDate, endDate);
        return transactionRepository.findTransactionsByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAssetAndDateRange(Long assetId, LocalDateTime startDate, LocalDateTime endDate) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        log.info("Fetching all transactions for asset ID: {} between {} and {}", assetId, startDate, endDate);
        return transactionRepository.findByAssetIdAndTradeDateBetween(assetId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTransactionCountByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        log.info("Counting transactions for asset ID: {}", assetId);
        return transactionRepository.findByAssetId(assetId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean transactionExistsByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        log.info("Checking existence of transactions for asset ID: {}", assetId);
        return !transactionRepository.findByAssetId(assetId).isEmpty();
    }
}
