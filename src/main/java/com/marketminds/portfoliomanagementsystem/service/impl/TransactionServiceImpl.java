package com.marketminds.portfoliomanagementsystem.service.impl;

import com.marketminds.portfoliomanagementsystem.model.Transaction;
import com.marketminds.portfoliomanagementsystem.repository.TransactionRepository;
import com.marketminds.portfoliomanagementsystem.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

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
        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return transactionRepository.findByAssetId(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAssetIdOrderByDateDesc(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return transactionRepository.findByAssetIdOrderByTradeDateDesc(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
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
        return transactionRepository.findByAssetIdAndTradeDateBetween(assetId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTransactionCountByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return transactionRepository.findByAssetId(assetId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean transactionExistsByAssetId(Long assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return !transactionRepository.findByAssetId(assetId).isEmpty();
    }
}
