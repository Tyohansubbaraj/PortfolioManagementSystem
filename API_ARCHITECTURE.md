# REST API Architecture Diagram & Overview

## High-Level Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                      CLIENT LAYER                              │
│  (Web Browser, Mobile App, Desktop Client, External APIs)      │
└──────────────────────────┬─────────────────────────────────────┘
                           │
                    HTTP/HTTPS (REST)
                           │
┌────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT APPLICATION                      │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              REST CONTROLLER LAYER (41 endpoints)        │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  AssetController  │ HoldingController │ TransactionCtrll │  │
│  │  (10 endpoints)   │  (9 endpoints)    │  (12 endpoints)  │  │
│  │                   │                   │                  │  │
│  │  WatchlistController (10 endpoints)                      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                        Delegates to                             │
│                           ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              SERVICE LAYER (Business Logic)              │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  AssetService    │ HoldingService    │ TransactionService│  │
│  │  + AssetServiceI │ + HoldingServiceI │ + TransactionSrvI│  │
│  │                  │                   │                  │  │
│  │  WatchlistService + WatchlistServiceImpl                 │  │
│  │                                                          │  │
│  │  ✓ Input Validation   ✓ Business Rules                  │  │
│  │  ✓ Error Handling     ✓ Calculations                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                        Uses                                     │
│                           ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │            REPOSITORY LAYER (Data Access)                │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  AssetRepository   HoldingRepository  TransactionRepository│  │
│  │                    WatchlistRepository                   │  │
│  │                                                          │  │
│  │  ✓ JPA Query Methods    ✓ Custom Queries               │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                     │
│                        Uses                                     │
│                           ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         DATA MODEL LAYER (JPA Entities)                  │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  Asset Entity     Holding Entity    Transaction Entity   │  │
│  │  • symbol         • totalQuantity   • quantity           │  │
│  │  • name           • avgBuyPrice     • price              │  │
│  │  • type           • asset (1-to-1)  • type (BUY/SELL)   │  │
│  │  • sector         • relationships   • tradeDate          │  │
│  │  • currentPrice                     • asset (Many-to-1)  │  │
│  │                                                          │  │
│  │  Watchlist Entity                                        │  │
│  │  • asset (Many-to-1)                                     │  │
│  │  • notes                                                 │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                     │
└───────────────────────────┼─────────────────────────────────────┘
                           │
                    JPA/Hibernate
                    SQL Queries
                           │
┌───────────────────────────┴─────────────────────────────────────┐
│                     MYSQL DATABASE                              │
│  portfoliomanagementsystem / portfoliotest                       │
│  ┌─────────────┬──────────────┬─────────────────┬──────────┐   │
│  │   assets    │   holdings   │  transactions   │ watchlist│   │
│  ├─────────────┼──────────────┼─────────────────┼──────────┤   │
│  │ id (PK)     │ id (PK)      │ id (PK)         │ id (PK)  │   │
│  │ symbol (UK) │ asset_id (FK)│ asset_id (FK)   │asset_id (FK)  │
│  │ name        │ totalQty     │ type            │ notes    │   │
│  │ type        │ avgBuyPrice  │ quantity        │ ...      │   │
│  │ sector      │ ...          │ price           │          │   │
│  │ currentPrice│              │ tradeDate       │          │   │
│  │ lastUpdated │              │ ...             │          │   │
│  └─────────────┴──────────────┴─────────────────┴──────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

## API Endpoint Hierarchy

```
/api
│
├── /assets (AssetController - 10 endpoints)
│   ├── POST   /assets                                  → Create
│   ├── GET    /assets                                  → List all
│   ├── GET    /assets/{id}                             → Get by ID
│   ├── GET    /assets/symbol/{symbol}                  → Get by symbol
│   ├── GET    /assets/type/{type}                      → Filter by type
│   ├── GET    /assets/sector/{sector}                  → Filter by sector
│   ├── GET    /assets/type/{type}/sector/{sector}      → Filter both
│   ├── PUT    /assets/{id}                             → Update
│   ├── DELETE /assets/{id}                             → Delete
│   └── GET    /assets/exists/{symbol}                  → Check exists
│
├── /holdings (HoldingController - 9 endpoints)
│   ├── POST   /holdings                                → Create
│   ├── GET    /holdings                                → List all
│   ├── GET    /holdings/{id}                           → Get by ID
│   ├── GET    /holdings/asset/{assetId}                → Get by asset
│   ├── GET    /holdings/exists/{assetId}               → Check exists
│   ├── POST   /holdings/{id}/buy                       → Buy shares
│   ├── POST   /holdings/{id}/sell                      → Sell shares
│   ├── PUT    /holdings/{id}                           → Update
│   └── DELETE /holdings/{id}                           → Delete
│
├── /transactions (TransactionController - 12 endpoints)
│   ├── POST   /transactions                            → Create
│   ├── GET    /transactions                            → List all
│   ├── GET    /transactions/{id}                       → Get by ID
│   ├── GET    /transactions/asset/{assetId}            → By asset
│   ├── GET    /transactions/asset/{assetId}/sorted     → By asset (sorted)
│   ├── GET    /transactions/type/{type}                → By type
│   ├── GET    /transactions/daterange                  → By date range
│   ├── GET    /transactions/asset/{assetId}/daterange  → Asset + date
│   ├── GET    /transactions/asset/{assetId}/count      → Count
│   ├── GET    /transactions/asset/{assetId}/exists     → Check exists
│   ├── PUT    /transactions/{id}                       → Update
│   └── DELETE /transactions/{id}                       → Delete
│
└── /watchlist (WatchlistController - 10 endpoints)
    ├── POST   /watchlist                               → Create
    ├── GET    /watchlist                               → List all
    ├── GET    /watchlist/{id}                          → Get by ID
    ├── GET    /watchlist/asset/{assetId}               → By asset
    ├── GET    /watchlist/asset/{assetId}/exists        → Check in list
    ├── POST   /watchlist/asset/{assetId}               → Add to list
    ├── DELETE /watchlist/asset/{assetId}               → Remove from list
    ├── PUT    /watchlist/{id}                          → Update
    ├── DELETE /watchlist/{id}                          → Delete
    └── GET    /watchlist/count                         → Count

TOTAL: 41 ENDPOINTS
```

---

## Data Flow Diagram

### Example: Create Asset Flow

```
┌──────────────────────────────────┐
│  POST /api/assets                │
│  {JSON Body: Asset Data}         │
└──────────────┬───────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  AssetController.createAsset()           │
│  ├─ Receive @RequestBody Asset           │
│  └─ Call assetService.createAsset()      │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  AssetService.createAsset()              │
│  ├─ Validate input (not null)            │
│  ├─ Check symbol not empty               │
│  ├─ Check symbol doesn't exist           │
│  ├─ Call repository.save()               │
│  └─ Return saved asset (or throw error)  │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  AssetRepository.save(asset)             │
│  ├─ Persist to database                  │
│  └─ Return saved entity with ID          │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  Controller catches response             │
│  ├─ If success: return 201 Created       │
│  ├─ If validation error: return 400      │
│  └─ If exception: return 500             │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  HTTP Response                           │
│  Status: 201 Created                     │
│  {JSON Body: Created Asset}              │
└──────────────────────────────────────────┘
```

---

## Request/Response Pattern

### Pattern 1: Successful Creation
```
REQUEST:
POST /api/assets
Content-Type: application/json

{
  "symbol": "AAPL",
  "name": "Apple",
  "type": "Stock",
  "sector": "Technology",
  "currentPrice": 150.50,
  "lastUpdated": "2026-02-01T12:00:00"
}

RESPONSE (201 Created):
{
  "id": 1,
  "symbol": "AAPL",
  "name": "Apple",
  "type": "Stock",
  "sector": "Technology",
  "currentPrice": 150.50,
  "lastUpdated": "2026-02-01T12:00:00"
}
```

### Pattern 2: Successful Retrieval
```
REQUEST:
GET /api/assets/1

RESPONSE (200 OK):
{
  "id": 1,
  "symbol": "AAPL",
  "name": "Apple",
  "type": "Stock",
  "sector": "Technology",
  "currentPrice": 150.50,
  "lastUpdated": "2026-02-01T12:00:00"
}
```

### Pattern 3: Not Found
```
REQUEST:
GET /api/assets/999

RESPONSE (404 Not Found):
(Empty body)
```

### Pattern 4: Validation Error
```
REQUEST:
POST /api/assets
{
  "symbol": "AAPL",
  ...duplicate...
}

RESPONSE (400 Bad Request):
(Error message in body or empty)
```

---

## Entity Relationships

```
┌─────────────────────────────────────────────────────────┐
│                    Asset (1)                            │
│  ┌───────────────────────────────────────────────────┐  │
│  │ - id (PK)                                         │  │
│  │ - symbol (UNIQUE)                                │  │
│  │ - name                                            │  │
│  │ - type (Stock, Bond, etc.)                        │  │
│  │ - sector                                          │  │
│  │ - currentPrice                                    │  │
│  │ - lastUpdated                                     │  │
│  └───────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
    1-to-1             1-to-many          1-to-many
        │                  │                  │
        ▼                  ▼                  ▼
┌────────────────┐  ┌──────────────────┐  ┌──────────────┐
│    Holding     │  │   Transaction    │  │   Watchlist  │
├────────────────┤  ├──────────────────┤  ├──────────────┤
│ - id (PK)      │  │ - id (PK)        │  │ - id (PK)    │
│ - asset_id(FK) │  │ - asset_id (FK)  │  │ - asset_id(FK)
│ - totalQty     │  │ - type (BUY/SELL)│  │ - notes      │
│ - avgBuyPrice  │  │ - quantity       │  │ - ...        │
│ - ...          │  │ - price          │  │              │
└────────────────┘  │ - tradeDate      │  └──────────────┘
                    │ - ...            │
                    └──────────────────┘
```

---

## HTTP Status Codes Used

```
2xx SUCCESS
├── 200 OK          → Successful GET/PUT
├── 201 Created     → Successful POST (resource created)
└── 204 No Content  → Successful DELETE or empty GET

4xx CLIENT ERROR
├── 400 Bad Request      → Validation errors, invalid input
└── 404 Not Found        → Resource doesn't exist

5xx SERVER ERROR
└── 500 Server Error     → Unexpected errors
```

---

## Error Handling Flow

```
┌──────────────────────────────────┐
│  Controller Method Called        │
└──────────────┬───────────────────┘
               │
               ▼
        ┌─────────────┐
        │ Try Block   │
        └──────┬──────┘
               │
        ┌──────────────────────────────┐
        │ Execute Service Method       │
        └──────┬───────────────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
    ▼                     ▼
┌─────────┐      ┌──────────────────────┐
│ Success │      │ Exception Thrown     │
└────┬────┘      └──────┬───────────────┘
     │                  │
     │         ┌────────┴─────────┐
     │         │                  │
     │         ▼                  ▼
     │    ┌─────────────┐   ┌──────────────────┐
     │    │ IllegalArg  │   │ Generic Exception│
     │    │Exception    │   └──────┬───────────┘
     │    └─────┬───────┘          │
     │          │                  │
     │     400  │         ┌────────┴─────────┐
     │     Bad  │         │                  │
     │     Req  │    5xx  │             404
     │          │  Server │             Not
     │          │  Error  │             Found
     │          │         │
     ▼          ▼         ▼                ▼
  ┌──────────────────────────────────────────────┐
  │  Return ResponseEntity with HTTP Status     │
  └──────────────────────────────────────────────┘
```

---

## Controller Method Signature Pattern

```java
@GetMapping("/{id}")
public ResponseEntity<Asset> getAssetById(@PathVariable Long id) {
    try {
        Optional<Asset> asset = assetService.getAssetById(id);
        return asset.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

┌─────────────────────────────────────────────────────────┐
│ Components:                                             │
│ ✓ @GetMapping - HTTP method and path                   │
│ ✓ @PathVariable - Extract from URL                     │
│ ✓ ResponseEntity - Flexible HTTP response              │
│ ✓ Try-catch - Error handling                           │
│ ✓ HttpStatus - Proper status codes                     │
└─────────────────────────────────────────────────────────┘
```

---

## Service Validation Layer Pattern

```java
public Asset createAsset(Asset asset) {
    // Validation 1: Null check
    if (asset == null) {
        throw new IllegalArgumentException("Asset cannot be null");
    }
    
    // Validation 2: Required field check
    if (asset.getSymbol() == null || asset.getSymbol().isEmpty()) {
        throw new IllegalArgumentException("Asset symbol cannot be null or empty");
    }
    
    // Validation 3: Business rule check
    if (assetRepository.findBySymbol(asset.getSymbol()).isPresent()) {
        throw new IllegalArgumentException("Asset with symbol already exists");
    }
    
    // All validations passed
    return assetRepository.save(asset);
}

┌────────────────────────────────────────────┐
│ Validation Levels:                         │
│ L1: Null/Type Safety                       │
│ L2: Required Fields                        │
│ L3: Business Rules                         │
│ L4: Database Constraints                   │
└────────────────────────────────────────────┘
```

---

## CORS Configuration

```java
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetController {
    // ...
}

┌──────────────────────────────────────────┐
│ CORS Settings Applied to ALL Controllers:│
├──────────────────────────────────────────┤
│ Allowed Origins: * (all)                 │
│ Max Age: 3600 seconds (1 hour)           │
│ Default Methods: GET, POST, PUT, DELETE  │
│ Default Headers: Content-Type, Accept    │
│                                          │
│ Production Note:                         │
│ Restrict origins to specific domains     │
│ in production environment                │
└──────────────────────────────────────────┘
```

---

## Summary

✅ **41 RESTful Endpoints**
✅ **4 Controllers** (Asset, Holding, Transaction, Watchlist)
✅ **Complete CRUD Operations**
✅ **Advanced Filtering**
✅ **Proper HTTP Status Codes**
✅ **Comprehensive Error Handling**
✅ **CORS Support**
✅ **Transaction Tracking**
✅ **Business Logic Integration**

**Status:** PRODUCTION READY ✅

