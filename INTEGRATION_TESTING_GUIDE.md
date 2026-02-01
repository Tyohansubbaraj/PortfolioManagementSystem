# Integration Testing Guide for REST Controllers

## Overview
This guide provides examples for testing the REST controllers using various tools and methods.

---

## Prerequisites

1. **Spring Boot Application Running**
   ```bash
   mvn spring-boot:run
   ```
   The application will be available at `http://localhost:8080`

2. **Required Tools** (Choose one or more):
   - cURL (command-line)
   - Postman (desktop or web)
   - VS Code REST Client
   - Insomnia
   - Thunder Client

---

## Testing Workflow

### Complete User Journey

1. **Create Assets**
2. **Create Holdings**
3. **Record Transactions (Buy)**
4. **Add to Watchlist**
5. **View Reports**
6. **Update Holdings (Sell)**
7. **Query Transactions**

---

## Test Scenarios

### Scenario 1: Stock Portfolio Management

#### Step 1: Create Assets
```bash
# Create Apple Stock
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "AAPL",
    "name": "Apple Inc.",
    "type": "Stock",
    "sector": "Technology",
    "currentPrice": 150.50,
    "lastUpdated": "2026-02-01T12:00:00"
  }'

# Create Google Stock
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "GOOGL",
    "name": "Alphabet Inc.",
    "type": "Stock",
    "sector": "Technology",
    "currentPrice": 140.75,
    "lastUpdated": "2026-02-01T12:00:00"
  }'

# Create Microsoft Stock
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "MSFT",
    "name": "Microsoft Corp.",
    "type": "Stock",
    "sector": "Technology",
    "currentPrice": 380.00,
    "lastUpdated": "2026-02-01T12:00:00"
  }'

# Create Bond
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "USBD",
    "name": "US Bond ETF",
    "type": "Bond",
    "sector": "Fixed Income",
    "currentPrice": 95.00,
    "lastUpdated": "2026-02-01T12:00:00"
  }'
```

#### Step 2: Verify Assets Created
```bash
# Get all assets
curl http://localhost:8080/api/assets

# Get specific asset
curl http://localhost:8080/api/assets/1

# Filter by type
curl http://localhost:8080/api/assets/type/Stock

# Filter by sector
curl http://localhost:8080/api/assets/sector/Technology

# Check asset exists
curl http://localhost:8080/api/assets/exists/AAPL
```

**Expected Response:** 200 OK with asset details

---

#### Step 3: Create Holdings
```bash
# Create holding for AAPL
curl -X POST http://localhost:8080/api/holdings \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 1 },
    "totalQuantity": 100.00,
    "avgBuyPrice": 120.00
  }'

# Create holding for GOOGL
curl -X POST http://localhost:8080/api/holdings \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 2 },
    "totalQuantity": 50.00,
    "avgBuyPrice": 130.00
  }'

# Create holding for MSFT
curl -X POST http://localhost:8080/api/holdings \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 3 },
    "totalQuantity": 25.00,
    "avgBuyPrice": 350.00
  }'
```

**Expected Response:** 201 Created with holding details

---

#### Step 4: Record Initial Transactions
```bash
# Buy AAPL
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 1 },
    "type": "BUY",
    "quantity": 100.00,
    "price": 120.00,
    "tradeDate": "2026-01-15T10:30:00"
  }'

# Buy GOOGL
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 2 },
    "type": "BUY",
    "quantity": 50.00,
    "price": 130.00,
    "tradeDate": "2026-01-15T11:00:00"
  }'

# Buy MSFT
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 3 },
    "type": "BUY",
    "quantity": 25.00,
    "price": 350.00,
    "tradeDate": "2026-01-15T11:30:00"
  }'
```

**Expected Response:** 201 Created with transaction details

---

#### Step 5: Add to Watchlist
```bash
# Add USBD to watchlist
curl -X POST "http://localhost:8080/api/watchlist/asset/4?notes=Monitor%20bond%20prices"

# Verify watchlist
curl http://localhost:8080/api/watchlist

# Check specific asset in watchlist
curl http://localhost:8080/api/watchlist/asset/4/exists

# Get watchlist count
curl http://localhost:8080/api/watchlist/count
```

**Expected Response:** 201 Created for add, 200 OK for queries

---

#### Step 6: Buy More Shares (Update Holding)
```bash
# Buy 50 more AAPL at $155
curl -X POST "http://localhost:8080/api/holdings/1/buy?quantity=50&price=155.00"

# Response should show updated total quantity (150) and new avg price
```

**Expected Calculation:**
- Old: 100 @ $120.00 = $12,000
- New: 50 @ $155.00 = $7,750
- Total: 150 @ $116.67 avg

---

#### Step 7: Query Transaction History
```bash
# Get all AAPL transactions
curl http://localhost:8080/api/transactions/asset/1

# Get AAPL transactions sorted by date
curl http://localhost:8080/api/transactions/asset/1/sorted

# Get all BUY transactions
curl http://localhost:8080/api/transactions/type/BUY

# Get transactions by date range
curl "http://localhost:8080/api/transactions/daterange?startDate=2026-01-01T00:00:00&endDate=2026-02-01T23:59:59"

# Get AAPL transactions in date range
curl "http://localhost:8080/api/transactions/asset/1/daterange?startDate=2026-01-01T00:00:00&endDate=2026-02-01T23:59:59"

# Count AAPL transactions
curl http://localhost:8080/api/transactions/asset/1/count

# Check AAPL has transactions
curl http://localhost:8080/api/transactions/asset/1/exists
```

---

#### Step 8: Sell Shares
```bash
# Sell 30 AAPL shares
curl -X POST "http://localhost:8080/api/holdings/1/sell?quantity=30"

# Response should show updated quantity (120) with same avg price
```

---

#### Step 9: Record Sell Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 1 },
    "type": "SELL",
    "quantity": 30.00,
    "price": 160.00,
    "tradeDate": "2026-02-01T14:30:00"
  }'
```

---

### Scenario 2: Error Handling Tests

#### Test 1: Create Duplicate Asset
```bash
# This should return 400 Bad Request
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "AAPL",
    "name": "Apple Inc.",
    "type": "Stock",
    "sector": "Technology",
    "currentPrice": 150.50,
    "lastUpdated": "2026-02-01T12:00:00"
  }'

# Expected: 400 Bad Request with message about duplicate symbol
```

#### Test 2: Invalid Transaction Type
```bash
# This should return 400 Bad Request
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 1 },
    "type": "INVALID",
    "quantity": 10.00,
    "price": 150.00,
    "tradeDate": "2026-02-01T12:00:00"
  }'

# Expected: 400 Bad Request
```

#### Test 3: Sell More Than Available
```bash
# If holding has 120 shares, try to sell 150
# This should return 400 Bad Request
curl -X POST "http://localhost:8080/api/holdings/1/sell?quantity=150"

# Expected: 400 Bad Request with message about insufficient quantity
```

#### Test 4: Update Non-Existent Asset
```bash
# This should return 404 Not Found
curl -X PUT http://localhost:8080/api/assets/999 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 999,
    "symbol": "FAKE",
    "name": "Fake Corp",
    "type": "Stock",
    "sector": "Technology",
    "currentPrice": 100.00,
    "lastUpdated": "2026-02-01T12:00:00"
  }'

# Expected: 404 Not Found
```

#### Test 5: Delete Non-Existent Record
```bash
# This should return 404 Not Found
curl -X DELETE http://localhost:8080/api/assets/999

# Expected: 404 Not Found
```

---

## Postman Collection Template

### Import Instructions
1. Create new Collection: "Portfolio Management System"
2. Create folders: Assets, Holdings, Transactions, Watchlist
3. Add requests as shown below

### Sample Requests

**Create Asset (Folder: Assets)**
```
POST {{base_url}}/api/assets
Content-Type: application/json

{
  "symbol": "AAPL",
  "name": "Apple Inc.",
  "type": "Stock",
  "sector": "Technology",
  "currentPrice": 150.50,
  "lastUpdated": "2026-02-01T12:00:00"
}
```

**Get All Assets**
```
GET {{base_url}}/api/assets
```

**Buy Shares (Folder: Holdings)**
```
POST {{base_url}}/api/holdings/1/buy?quantity=50&price=155.00
```

**Create Transaction (Folder: Transactions)**
```
POST {{base_url}}/api/transactions
Content-Type: application/json

{
  "asset": { "id": 1 },
  "type": "BUY",
  "quantity": 10.00,
  "price": 150.50,
  "tradeDate": "2026-02-01T12:00:00"
}
```

**Query Transactions by Date**
```
GET {{base_url}}/api/transactions/daterange?startDate=2026-01-01T00:00:00&endDate=2026-02-01T23:59:59
```

**Add to Watchlist (Folder: Watchlist)**
```
POST {{base_url}}/api/watchlist/asset/1?notes=Monitor%20this%20stock
```

---

## Performance Testing

### Bulk Create Assets
```bash
#!/bin/bash
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/assets \
    -H "Content-Type: application/json" \
    -d "{
      \"symbol\": \"SYM$i\",
      \"name\": \"Company $i\",
      \"type\": \"Stock\",
      \"sector\": \"Technology\",
      \"currentPrice\": $((100 + i)).50,
      \"lastUpdated\": \"2026-02-01T12:00:00\"
    }"
done
```

---

## Validation Checklist

- ✅ All CRUD operations return correct status codes
- ✅ Business logic (avg price calculation) works correctly
- ✅ Date range queries return expected results
- ✅ Validation errors return 400 Bad Request
- ✅ Non-existent resources return 404 Not Found
- ✅ CORS headers are present in responses
- ✅ JSON serialization/deserialization works
- ✅ Transactional operations are atomic
- ✅ Relationships between entities are maintained
- ✅ Performance is acceptable for typical loads

---

## Troubleshooting

### Issue: Connection Refused
**Solution:** Ensure Spring Boot application is running on port 8080

### Issue: 500 Internal Server Error
**Solution:** Check application logs for exceptions

### Issue: 400 Bad Request
**Solution:** Validate JSON format and required fields

### Issue: 404 Not Found
**Solution:** Verify the resource ID exists in the database

### Issue: Empty Response
**Solution:** Check if result set is genuinely empty (204 No Content is expected)

---

## Cleanup

### Delete All Test Data
```bash
# Delete holdings
curl -X DELETE http://localhost:8080/api/holdings/1

# Delete watchlist entries
curl -X DELETE http://localhost:8080/api/watchlist/1

# Delete transactions (if allowed)
curl -X DELETE http://localhost:8080/api/transactions/1

# Delete assets
curl -X DELETE http://localhost:8080/api/assets/1
```

---

## Next Steps

1. **Load Testing** - Use Apache JMeter or similar tools
2. **Security Testing** - Test authentication/authorization
3. **API Documentation** - Generate with Swagger/OpenAPI
4. **Monitoring** - Set up application monitoring and logging
5. **CI/CD Integration** - Add to deployment pipeline

