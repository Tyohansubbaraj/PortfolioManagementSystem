-- Portfolio Management System Database Schema
-- Created for testing and development purposes

-- Create the portfolio database if it doesn't exist
CREATE DATABASE IF NOT EXISTS portfolio;

-- Use the portfolio database
USE portfolio;

-- Drop tables if they exist (for development/testing)
DROP TABLE IF EXISTS watchlist;
DROP TABLE IF EXISTS holdings;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS assets;

-- 1. Assets Table (The Catalog)
CREATE TABLE assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    sector VARCHAR(50) NOT NULL,
    current_price DECIMAL(18,4) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Transactions Table (The Action Log)
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('BUY', 'SELL')),
    quantity DECIMAL(18,4) NOT NULL,
    price DECIMAL(18,4) NOT NULL,
    trade_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
);

-- 3. Holdings Table (Current Portfolio)
CREATE TABLE holdings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT UNIQUE NOT NULL,
    total_quantity DECIMAL(18,4) NOT NULL,
    avg_buy_price DECIMAL(18,4) NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
);

-- 4. Watchlist Table (Monitoring)
CREATE TABLE watchlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    notes TEXT,
    FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_transactions_asset_id ON transactions(asset_id);
CREATE INDEX idx_holdings_asset_id ON holdings(asset_id);
CREATE INDEX idx_watchlist_asset_id ON watchlist(asset_id);
CREATE INDEX idx_assets_symbol ON assets(symbol);
CREATE INDEX idx_assets_sector ON assets(sector);

-- =====================================================
-- Sample Data Insertion
-- =====================================================

-- Insert sample assets (The Catalog)
INSERT INTO assets (symbol, name, sector, current_price, last_updated) VALUES
('AAPL', 'Apple Inc.', 'Technology', 195.50, NOW()),
('MSFT', 'Microsoft Corporation', 'Technology', 380.25, NOW()),
('GOOGL', 'Alphabet Inc.', 'Technology', 155.80, NOW()),
('AMZN', 'Amazon.com Inc.', 'Consumer', 180.75, NOW()),
('TSLA', 'Tesla Inc.', 'Automotive', 245.30, NOW()),
('META', 'Meta Platforms Inc.', 'Technology', 520.45, NOW()),
('NVDA', 'NVIDIA Corporation', 'Technology', 875.20, NOW()),
('JPM', 'JPMorgan Chase & Co.', 'Finance', 195.10, NOW()),
('V', 'Visa Inc.', 'Finance', 280.60, NOW()),
('JNJ', 'Johnson & Johnson', 'Healthcare', 155.45, NOW());

-- Insert sample transactions (The Action Log)
-- Transactions for AAPL (asset_id = 1)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(1, 'BUY', 100, 150.00, DATE_SUB(NOW(), INTERVAL 6 MONTH)),
(1, 'BUY', 50, 165.00, DATE_SUB(NOW(), INTERVAL 3 MONTH));

-- Transactions for MSFT (asset_id = 2)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(2, 'BUY', 75, 320.00, DATE_SUB(NOW(), INTERVAL 5 MONTH));

-- Transactions for GOOGL (asset_id = 3)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(3, 'BUY', 30, 120.00, DATE_SUB(NOW(), INTERVAL 4 MONTH)),
(3, 'SELL', 10, 155.00, DATE_SUB(NOW(), INTERVAL 1 MONTH));

-- Transactions for AMZN (asset_id = 4)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(4, 'BUY', 40, 150.00, DATE_SUB(NOW(), INTERVAL 2 MONTH));

-- Transactions for TSLA (asset_id = 5)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(5, 'BUY', 20, 200.00, DATE_SUB(NOW(), INTERVAL 3 MONTH)),
(5, 'BUY', 15, 230.00, DATE_SUB(NOW(), INTERVAL 1 MONTH));

-- Transactions for META (asset_id = 6)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(6, 'BUY', 10, 450.00, DATE_SUB(NOW(), INTERVAL 2 MONTH));

-- Transactions for NVDA (asset_id = 7)
INSERT INTO transactions (asset_id, type, quantity, price, trade_date) VALUES
(7, 'BUY', 5, 750.00, DATE_SUB(NOW(), INTERVAL 1 MONTH));

-- Insert sample holdings (Current Portfolio)
-- Holdings must reference existing assets from the assets table
INSERT INTO holdings (asset_id, total_quantity, avg_buy_price) VALUES
(1, 150, 155.00),      -- AAPL
(2, 75, 320.00),       -- MSFT
(3, 20, 130.00),       -- GOOGL
(4, 40, 150.00),       -- AMZN
(5, 35, 215.71),       -- TSLA
(6, 10, 450.00),       -- META
(7, 5, 750.00);        -- NVDA

-- Insert sample watchlist items (Monitoring)
-- Watchlist references existing assets
INSERT INTO watchlist (asset_id, notes) VALUES
(8, 'Strong banking sector, monitor quarterly earnings'),
(9, 'Visa dominates payment processing, good dividend'),
(10, 'Diversified healthcare, recession-proof');

