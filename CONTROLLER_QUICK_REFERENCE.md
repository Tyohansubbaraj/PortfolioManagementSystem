# REST Controllers - Quick Reference Guide

## Controllers Overview

| Controller | Base Path | Purpose |
|-----------|-----------|---------|
| AssetController | `/api/assets` | Manage investment assets (stocks, bonds, etc.) |
| HoldingController | `/api/holdings` | Manage asset holdings and quantities |
| TransactionController | `/api/transactions` | Record buy/sell transactions |
| WatchlistController | `/api/watchlist` | Manage watchlist of assets |

---

## AssetController - 10 Endpoints

```
POST   /api/assets                                    → Create asset (201)
GET    /api/assets                                    → Get all assets (200/204)
GET    /api/assets/{id}                               → Get by ID (200/404)
GET    /api/assets/symbol/{symbol}                    → Get by symbol (200/404)
GET    /api/assets/type/{type}                        → Filter by type (200/204)
GET    /api/assets/sector/{sector}                    → Filter by sector (200/204)
GET    /api/assets/type/{type}/sector/{sector}        → Filter by both (200/204)
PUT    /api/assets/{id}                               → Update (200/400/404)
DELETE /api/assets/{id}                               → Delete (204/404)
GET    /api/assets/exists/{symbol}                    → Check exists (200)
```

---

## HoldingController - 9 Endpoints

```
POST   /api/holdings                                  → Create (201)
GET    /api/holdings                                  → Get all (200/204)
GET    /api/holdings/{id}                             → Get by ID (200/404)
GET    /api/holdings/asset/{assetId}                  → Get by asset (200/404)
GET    /api/holdings/exists/{assetId}                 → Check exists (200)
POST   /api/holdings/{id}/buy?quantity=X&price=Y     → Buy shares (200/400)
POST   /api/holdings/{id}/sell?quantity=X            → Sell shares (200/400)
PUT    /api/holdings/{id}                             → Update (200/400/404)
DELETE /api/holdings/{id}                             → Delete (204/404)
```

**Special Buy/Sell Endpoints:**
- **Buy:** Adds quantity and recalculates average price
- **Sell:** Reduces quantity (validates sufficient balance)

---

## TransactionController - 12 Endpoints

```
POST   /api/transactions                              → Create (201)
GET    /api/transactions                              → Get all (200/204)
GET    /api/transactions/{id}                         → Get by ID (200/404)
GET    /api/transactions/asset/{assetId}              → By asset (200/204)
GET    /api/transactions/asset/{assetId}/sorted       → By asset (desc) (200/204)
GET    /api/transactions/type/{type}                  → By type BUY/SELL (200/204)
GET    /api/transactions/daterange                    → By date range (200/204)
GET    /api/transactions/asset/{assetId}/daterange    → Asset + date (200/204)
GET    /api/transactions/asset/{assetId}/count        → Count (200)
GET    /api/transactions/asset/{assetId}/exists       → Check exists (200)
PUT    /api/transactions/{id}                         → Update (200/400/404)
DELETE /api/transactions/{id}                         → Delete (204/404)
```

**Date Range Query:**
```
?startDate=2026-01-01T00:00:00&endDate=2026-02-01T23:59:59
```

---

## WatchlistController - 10 Endpoints

```
POST   /api/watchlist                                 → Create (201)
GET    /api/watchlist                                 → Get all (200/204)
GET    /api/watchlist/{id}                            → Get by ID (200/404)
GET    /api/watchlist/asset/{assetId}                 → By asset (200/404)
GET    /api/watchlist/asset/{assetId}/exists          → Check in list (200)
POST   /api/watchlist/asset/{assetId}                 → Add to list (201)
DELETE /api/watchlist/asset/{assetId}                 → Remove from list (204/404)
PUT    /api/watchlist/{id}                            → Update (200/400/404)
DELETE /api/watchlist/{id}                            → Delete (204/404)
GET    /api/watchlist/count                           → Get count (200)
```

**Add to Watchlist Query:**
```
POST /api/watchlist/asset/1?notes=Optional%20notes
```

---

## Common Request Formats

### Asset
```json
{
  "symbol": "AAPL",
  "name": "Apple Inc.",
  "type": "Stock",
  "sector": "Technology",
  "currentPrice": 150.50,
  "lastUpdated": "2026-02-01T12:00:00"
}
```

### Holding
```json
{
  "asset": { "id": 1 },
  "totalQuantity": 100.00,
  "avgBuyPrice": 120.00
}
```

### Transaction
```json
{
  "asset": { "id": 1 },
  "type": "BUY",
  "quantity": 10.00,
  "price": 150.50,
  "tradeDate": "2026-02-01T12:00:00"
}
```

### Watchlist
```json
{
  "asset": { "id": 1 },
  "notes": "Good tech stock"
}
```

---

## HTTP Status Codes Reference

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | GET successful, PUT successful |
| 201 | Created | POST successful (resource created) |
| 204 | No Content | DELETE successful, empty GET result |
| 400 | Bad Request | Invalid input, validation error |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Unexpected error |

---

## Error Response Pattern

All endpoints use consistent error handling:

```
Error Code → HTTP Status Code
├─ Validation Error → 400
├─ Not Found → 404
└─ Server Error → 500
```

---

## CORS Policy

✅ All controllers have CORS enabled:
- Allowed Origins: `*` (all)
- Max Age: 3600 seconds (1 hour)

---

## Curl Examples

### 1. Create Asset
```bash
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "symbol":"AAPL",
    "name":"Apple",
    "type":"Stock",
    "sector":"Technology",
    "currentPrice":150.50,
    "lastUpdated":"2026-02-01T12:00:00"
  }'
```

### 2. Get All Assets
```bash
curl http://localhost:8080/api/assets
```

### 3. Get Asset by Symbol
```bash
curl http://localhost:8080/api/assets/symbol/AAPL
```

### 4. Create Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset":{"id":1},
    "type":"BUY",
    "quantity":10.00,
    "price":150.50,
    "tradeDate":"2026-02-01T12:00:00"
  }'
```

### 5. Buy Shares
```bash
curl -X POST "http://localhost:8080/api/holdings/1/buy?quantity=50&price=150.00"
```

### 6. Sell Shares
```bash
curl -X POST "http://localhost:8080/api/holdings/1/sell?quantity=20"
```

### 7. Add to Watchlist
```bash
curl -X POST "http://localhost:8080/api/watchlist/asset/1?notes=Promising%20stock"
```

### 8. Get Transactions by Date Range
```bash
curl "http://localhost:8080/api/transactions/daterange?startDate=2026-01-01T00:00:00&endDate=2026-02-01T23:59:59"
```

### 9. Get All Watchlist
```bash
curl http://localhost:8080/api/watchlist
```

### 10. Check Asset in Watchlist
```bash
curl http://localhost:8080/api/watchlist/asset/1/exists
```

---

## Endpoint Statistics

| Controller | GET | POST | PUT | DELETE | Total |
|-----------|-----|------|-----|--------|-------|
| Asset | 7 | 1 | 1 | 1 | **10** |
| Holding | 5 | 2 | 1 | 1 | **9** |
| Transaction | 9 | 1 | 1 | 1 | **12** |
| Watchlist | 5 | 2 | 1 | 2 | **10** |
| **TOTAL** | **26** | **6** | **4** | **5** | **41** |

---

## Key Features

✅ **Full CRUD Operations** - Create, Read, Update, Delete for all entities
✅ **Advanced Filtering** - Filter by type, sector, date range, asset, etc.
✅ **Business Logic** - Auto-calculation of average buy price on purchases
✅ **Validation** - Input validation with proper error messages
✅ **Transaction Support** - Record and track all buy/sell transactions
✅ **Watchlist Management** - Track interesting assets for monitoring
✅ **Error Handling** - Comprehensive exception handling with HTTP status codes
✅ **CORS Enabled** - Support for cross-origin requests
✅ **RESTful Design** - Follows REST principles and conventions
✅ **Documentation** - Comprehensive Javadoc for all methods

---

## Integration Points

Each controller integrates with:
- **Service Layer** - Handles business logic
- **Repository Layer** - Manages database operations
- **Model Layer** - Data entity definitions
- **Spring Framework** - Provides dependency injection, routing, etc.

---

## File Locations

```
📁 src/main/java/com/marketminds/portfoliomanagementsystem/controller/
├── AssetController.java
├── HoldingController.java
├── TransactionController.java
└── WatchlistController.java
```

---

## Next Steps

1. **Start Spring Boot Application**
   ```bash
   mvn spring-boot:run
   ```

2. **Access Swagger UI** (if available)
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Test Endpoints**
   - Use cURL, Postman, or other REST clients
   - Start with simple GET requests
   - Progress to POST/PUT/DELETE operations

4. **Monitor Logs**
   - Check console for any errors
   - Monitor database operations

---

## Support

For detailed information about each endpoint, refer to:
- `API_DOCUMENTATION.md` - Complete API reference
- `CONTROLLER_IMPLEMENTATION_SUMMARY.md` - Implementation details
- Javadoc comments in source code

