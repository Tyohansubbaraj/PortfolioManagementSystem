-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: portfolio
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `assets`
--

DROP TABLE IF EXISTS `assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `symbol` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` varchar(50) NOT NULL,
  `sector` varchar(50) NOT NULL,
  `current_price` decimal(18,4) NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `symbol` (`symbol`),
  KEY `idx_assets_symbol` (`symbol`),
  KEY `idx_assets_sector` (`sector`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assets`
--

LOCK TABLES `assets` WRITE;
/*!40000 ALTER TABLE `assets` DISABLE KEYS */;
INSERT INTO `assets` VALUES (1,'AAPL','Apple Inc.','STOCK','Technology',269.4800,'2026-02-04 05:28:01'),(2,'MSFT','Microsoft Corporation','STOCK','Technology',411.2100,'2026-02-04 05:28:01'),(3,'GOOGL','Alphabet Inc.','STOCK','Technology',339.7100,'2026-02-04 05:28:01'),(4,'AMZN','Amazon.com Inc.','STOCK','Consumer',238.6200,'2026-02-04 05:28:01'),(5,'TSLA','Tesla Inc.','STOCK','Automotive',421.9600,'2026-02-04 05:28:01'),(6,'META','Meta Platforms Inc.','STOCK','Technology',691.7000,'2026-02-04 05:28:01'),(7,'NVDA','NVIDIA Corporation','STOCK','Technology',180.3400,'2026-02-04 05:28:01'),(8,'JPM','JPMorgan Chase & Co.','STOCK','Finance',314.8500,'2026-02-04 05:28:01'),(9,'V','Visa Inc.','STOCK','Finance',328.9300,'2026-02-04 05:28:01'),(10,'JNJ','Johnson & Johnson','STOCK','Healthcare',233.1000,'2026-02-04 05:28:01'),(11,'UST','US Treasury Bond 10Y','BOND','Fixed Income',43.2900,'2026-02-04 05:28:01'),(12,'VTSAX','Vanguard Total Stock Market Index','ETF','Mixed',165.4500,'2026-02-04 05:28:01');
/*!40000 ALTER TABLE `assets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `holdings`
--

DROP TABLE IF EXISTS `holdings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `holdings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asset_id` bigint NOT NULL,
  `total_quantity` decimal(18,4) NOT NULL,
  `avg_buy_price` decimal(18,4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `asset_id` (`asset_id`),
  KEY `idx_holdings_asset_id` (`asset_id`),
  CONSTRAINT `holdings_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `holdings`
--

LOCK TABLES `holdings` WRITE;
/*!40000 ALTER TABLE `holdings` DISABLE KEYS */;
INSERT INTO `holdings` VALUES (1,1,151.0000,155.7581),(2,2,75.0000,320.0000),(3,3,20.0000,130.0000),(4,4,40.0000,150.0000),(5,5,35.0000,215.7100),(6,6,10.0000,450.0000),(7,7,5.0000,750.0000),(8,12,100.0000,245.7500);
/*!40000 ALTER TABLE `holdings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asset_id` bigint NOT NULL,
  `type` varchar(10) NOT NULL,
  `quantity` decimal(18,4) NOT NULL,
  `price` decimal(18,4) NOT NULL,
  `trade_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_transactions_asset_id` (`asset_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE CASCADE,
  CONSTRAINT `transactions_chk_1` CHECK ((`type` in (_utf8mb4'BUY',_utf8mb4'SELL')))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,1,'BUY',100.0000,150.0000,'2025-08-02 05:11:53'),(2,1,'BUY',50.0000,165.0000,'2025-11-02 05:11:53'),(3,2,'BUY',75.0000,320.0000,'2025-09-02 05:11:53'),(4,3,'BUY',30.0000,120.0000,'2025-10-02 05:11:53'),(5,3,'SELL',10.0000,155.0000,'2026-01-02 05:11:53'),(6,4,'BUY',40.0000,150.0000,'2025-12-02 05:11:53'),(7,5,'BUY',20.0000,200.0000,'2025-11-02 05:11:53'),(8,5,'BUY',15.0000,230.0000,'2026-01-02 05:11:53'),(9,6,'BUY',10.0000,450.0000,'2025-12-02 05:11:53'),(10,7,'BUY',5.0000,750.0000,'2026-01-02 05:11:53'),(11,12,'BUY',100.0000,245.7500,'2026-02-04 04:08:41'),(12,1,'BUY',1.0000,269.4800,'2026-02-04 05:23:17');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watchlist`
--

DROP TABLE IF EXISTS `watchlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watchlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asset_id` bigint NOT NULL,
  `notes` text,
  PRIMARY KEY (`id`),
  KEY `idx_watchlist_asset_id` (`asset_id`),
  CONSTRAINT `watchlist_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watchlist`
--

LOCK TABLES `watchlist` WRITE;
/*!40000 ALTER TABLE `watchlist` DISABLE KEYS */;
INSERT INTO `watchlist` VALUES (1,8,'Strong banking sector, monitor quarterly earnings'),(2,9,'Visa dominates payment processing, good dividend'),(3,10,'Diversified healthcare, recession-proof');
/*!40000 ALTER TABLE `watchlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'portfolio'
--

--
-- Dumping routines for database 'portfolio'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-04  5:28:23
