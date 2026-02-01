package com.marketminds.portfoliomanagementsystem.service.impl;

import com.marketminds.portfoliomanagementsystem.model.Asset;
import com.marketminds.portfoliomanagementsystem.repository.AssetRepository;
import com.marketminds.portfoliomanagementsystem.service.AssetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public Asset createAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset cannot be null");
        }
        if (asset.getSymbol() == null || asset.getSymbol().isEmpty()) {
            throw new IllegalArgumentException("Asset symbol cannot be null or empty");
        }
        if (assetRepository.findBySymbol(asset.getSymbol()).isPresent()) {
            throw new IllegalArgumentException("Asset with symbol '" + asset.getSymbol() + "' already exists");
        }
        return assetRepository.save(asset);
    }

    @Override
    public Asset updateAsset(Asset asset) {
        if (asset == null || asset.getId() == null) {
            throw new IllegalArgumentException("Asset and Asset ID cannot be null");
        }
        if (!assetRepository.existsById(asset.getId())) {
            throw new IllegalArgumentException("Asset with ID " + asset.getId() + " does not exist");
        }
        return assetRepository.save(asset);
    }

    @Override
    public void deleteAsset(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("Asset with ID " + id + " does not exist");
        }
        assetRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Asset> getAssetById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        return assetRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Asset> getAssetBySymbol(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        return assetRepository.findBySymbol(symbol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        return assetRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsBySector(String sector) {
        if (sector == null || sector.isEmpty()) {
            throw new IllegalArgumentException("Sector cannot be null or empty");
        }
        return assetRepository.findBySector(sector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByTypeAndSector(String type, String sector) {
        if (type == null || type.isEmpty() || sector == null || sector.isEmpty()) {
            throw new IllegalArgumentException("Type and sector cannot be null or empty");
        }
        return assetRepository.findByTypeAndSector(type, sector);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean assetExistsBySymbol(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        return assetRepository.findBySymbol(symbol).isPresent();
    }
}
