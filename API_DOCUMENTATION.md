# Portfolio Management System - REST API Documentation

## Base URL
```
http://localhost:8080/api
```

---

## Asset Controller
**Base Path:** `/api/assets`

### 1. Create Asset
- **Endpoint:** `POST /assets`
- **Description:** Create a new asset
- **Request Body:**
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
- **Response:** `201 Created`
```json
{
  "id": 1,
  "symbol": "AAPL",
  "name": "Apple Inc.",
  "type": "Stock",
  "sector": "Technology",
  "currentPrice": 150.50,
  "lastUpdated": "2026-02-01T12:00:00"
}
```

### 2. Get All Assets
- **Endpoint:** `GET /assets`
- **Description:** Retrieve all assets
- **Response:** `200 OK`

### 3. Get Asset by ID
- **Endpoint:** `GET /assets/{id}`
- **Description:** Retrieve a specific asset by ID
- **Parameters:** `id` (Long) - Asset ID
- **Response:** `200 OK` or `404 Not Found`

### 4. Get Asset by Symbol
- **Endpoint:** `GET /assets/symbol/{symbol}`
- **Description:** Retrieve asset by stock symbol
- **Parameters:** `symbol` (String) - Stock symbol
- **Response:** `200 OK` or `404 Not Found`

### 5. Get Assets by Type
- **Endpoint:** `GET /assets/type/{type}`
- **Description:** Retrieve all assets of a specific type
- **Parameters:** `type` (String) - Asset type (e.g., Stock, Bond)
- **Response:** `200 OK` or `204 No Content`

### 6. Get Assets by Sector
- **Endpoint:** `GET /assets/sector/{sector}`
- **Description:** Retrieve all assets in a specific sector
- **Parameters:** `sector` (String) - Sector name
- **Response:** `200 OK` or `204 No Content`

### 7. Get Assets by Type and Sector
- **Endpoint:** `GET /assets/type/{type}/sector/{sector}`
- **Description:** Retrieve assets matching both type and sector
- **Parameters:** 
  - `type` (String) - Asset type
  - `sector` (String) - Sector name
- **Response:** `200 OK` or `204 No Content`

### 8. Update Asset
- **Endpoint:** `PUT /assets/{id}`
- **Description:** Update an existing asset
- **Parameters:** `id` (Long) - Asset ID
- **Request Body:** Asset object with updated fields
- **Response:** `200 OK` or `400 Bad Request` or `404 Not Found`

### 9. Delete Asset
- **Endpoint:** `DELETE /assets/{id}`
- **Description:** Delete an asset
- **Parameters:** `id` (Long) - Asset ID
- **Response:** `204 No Content` or `404 Not Found`

### 10. Check Asset Exists by Symbol
- **Endpoint:** `GET /assets/exists/{symbol}`
- **Description:** Check if an asset with the given symbol exists
- **Parameters:** `symbol` (String) - Stock symbol
- **Response:** `200 OK` with boolean value

---

## Holding Controller
**Base Path:** `/api/holdings`

### 1. Create Holding
- **Endpoint:** `POST /holdings`
- **Description:** Create a new holding for an asset
- **Request Body:**
```json
{
  "asset": { "id": 1 },
  "totalQuantity": 100.00,
  "avgBuyPrice": 120.00
}
```
- **Response:** `201 Created`

### 2. Get All Holdings
- **Endpoint:** `GET /holdings`
- **Description:** Retrieve all holdings
- **Response:** `200 OK` or `204 No Content`

### 3. Get Holding by ID
- **Endpoint:** `GET /holdings/{id}`
- **Description:** Retrieve a specific holding by ID
- **Parameters:** `id` (Long) - Holding ID
- **Response:** `200 OK` or `404 Not Found`

### 4. Get Holding by Asset ID
- **Endpoint:** `GET /holdings/asset/{assetId}`
- **Description:** Retrieve holding for a specific asset
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` or `404 Not Found`

### 5. Check Holding Exists
- **Endpoint:** `GET /holdings/exists/{assetId}`
- **Description:** Check if a holding exists for an asset
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` with boolean value

### 6. Update Holding on Buy
- **Endpoint:** `POST /holdings/{id}/buy`
- **Description:** Update holding when buying more shares (recalculates average buy price)
- **Parameters:**
  - `id` (Long) - Holding ID
  - `quantity` (BigDecimal) - Quantity to buy
  - `price` (BigDecimal) - Price per unit
- **Query Params:** `?quantity=50&price=150.00`
- **Response:** `200 OK`
```json
{
  "id": 1,
  "asset": { "id": 1 },
  "totalQuantity": 150.00,
  "avgBuyPrice": 116.67
}
```

### 7. Update Holding on Sell
- **Endpoint:** `POST /holdings/{id}/sell`
- **Description:** Update holding when selling shares
- **Parameters:**
  - `id` (Long) - Holding ID
  - `quantity` (BigDecimal) - Quantity to sell
- **Query Params:** `?quantity=30`
- **Response:** `200 OK`

### 8. Update Holding
- **Endpoint:** `PUT /holdings/{id}`
- **Description:** Update an existing holding
- **Parameters:** `id` (Long) - Holding ID
- **Request Body:** Holding object with updated fields
- **Response:** `200 OK` or `400 Bad Request`

### 9. Delete Holding
- **Endpoint:** `DELETE /holdings/{id}`
- **Description:** Delete a holding
- **Parameters:** `id` (Long) - Holding ID
- **Response:** `204 No Content` or `404 Not Found`

---

## Transaction Controller
**Base Path:** `/api/transactions`

### 1. Create Transaction
- **Endpoint:** `POST /transactions`
- **Description:** Create a new transaction (BUY or SELL)
- **Request Body:**
```json
{
  "asset": { "id": 1 },
  "type": "BUY",
  "quantity": 10.00,
  "price": 150.50,
  "tradeDate": "2026-02-01T12:00:00"
}
```
- **Response:** `201 Created`

### 2. Get All Transactions
- **Endpoint:** `GET /transactions`
- **Description:** Retrieve all transactions
- **Response:** `200 OK` or `204 No Content`

### 3. Get Transaction by ID
- **Endpoint:** `GET /transactions/{id}`
- **Description:** Retrieve a specific transaction by ID
- **Parameters:** `id` (Long) - Transaction ID
- **Response:** `200 OK` or `404 Not Found`

### 4. Get Transactions by Asset ID
- **Endpoint:** `GET /transactions/asset/{assetId}`
- **Description:** Retrieve all transactions for an asset
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` or `204 No Content`

### 5. Get Transactions by Asset ID (Sorted by Date)
- **Endpoint:** `GET /transactions/asset/{assetId}/sorted`
- **Description:** Retrieve transactions for an asset ordered by date (most recent first)
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` or `204 No Content`

### 6. Get Transactions by Type
- **Endpoint:** `GET /transactions/type/{type}`
- **Description:** Retrieve all transactions of a specific type
- **Parameters:** `type` (String) - Transaction type (BUY or SELL)
- **Response:** `200 OK` or `204 No Content`

### 7. Get Transactions by Date Range
- **Endpoint:** `GET /transactions/daterange`
- **Description:** Retrieve transactions within a date range
- **Query Parameters:**
  - `startDate` (String) - Start date in ISO format (e.g., 2026-01-01T00:00:00)
  - `endDate` (String) - End date in ISO format
- **Response:** `200 OK` or `204 No Content`

### 8. Get Transactions by Asset and Date Range
- **Endpoint:** `GET /transactions/asset/{assetId}/daterange`
- **Description:** Retrieve transactions for an asset within a date range
- **Path Parameters:** `assetId` (Long) - Asset ID
- **Query Parameters:**
  - `startDate` (String) - Start date in ISO format
  - `endDate` (String) - End date in ISO format
- **Response:** `200 OK` or `204 No Content`

### 9. Get Transaction Count by Asset ID
- **Endpoint:** `GET /transactions/asset/{assetId}/count`
- **Description:** Get the number of transactions for an asset
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` with count value

### 10. Check Transactions Exist by Asset ID
- **Endpoint:** `GET /transactions/asset/{assetId}/exists`
- **Description:** Check if any transactions exist for an asset
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` with boolean value

### 11. Update Transaction
- **Endpoint:** `PUT /transactions/{id}`
- **Description:** Update an existing transaction
- **Parameters:** `id` (Long) - Transaction ID
- **Request Body:** Transaction object with updated fields
- **Response:** `200 OK` or `400 Bad Request` or `404 Not Found`

### 12. Delete Transaction
- **Endpoint:** `DELETE /transactions/{id}`
- **Description:** Delete a transaction
- **Parameters:** `id` (Long) - Transaction ID
- **Response:** `204 No Content` or `404 Not Found`

---

## Watchlist Controller
**Base Path:** `/api/watchlist`

### 1. Create Watchlist Entry
- **Endpoint:** `POST /watchlist`
- **Description:** Create a new watchlist entry
- **Request Body:**
```json
{
  "asset": { "id": 1 },
  "notes": "Good tech stock to monitor"
}
```
- **Response:** `201 Created`

### 2. Get All Watchlist Entries
- **Endpoint:** `GET /watchlist`
- **Description:** Retrieve all watchlist entries
- **Response:** `200 OK` or `204 No Content`

### 3. Get Watchlist Entry by ID
- **Endpoint:** `GET /watchlist/{id}`
- **Description:** Retrieve a specific watchlist entry by ID
- **Parameters:** `id` (Long) - Watchlist entry ID
- **Response:** `200 OK` or `404 Not Found`

### 4. Get Watchlist Entry by Asset ID
- **Endpoint:** `GET /watchlist/asset/{assetId}`
- **Description:** Retrieve watchlist entry for a specific asset
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` or `404 Not Found`

### 5. Check if Asset is in Watchlist
- **Endpoint:** `GET /watchlist/asset/{assetId}/exists`
- **Description:** Check if an asset is in the watchlist
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `200 OK` with boolean value

### 6. Add Asset to Watchlist
- **Endpoint:** `POST /watchlist/asset/{assetId}`
- **Description:** Add an asset to the watchlist
- **Path Parameters:** `assetId` (Long) - Asset ID
- **Query Parameters:** `notes` (String, optional) - Notes for the watchlist entry
- **Response:** `201 Created`
```
POST /api/watchlist/asset/1?notes=Promising%20tech%20company
```

### 7. Remove Asset from Watchlist
- **Endpoint:** `DELETE /watchlist/asset/{assetId}`
- **Description:** Remove an asset from the watchlist
- **Parameters:** `assetId` (Long) - Asset ID
- **Response:** `204 No Content` or `404 Not Found`

### 8. Update Watchlist Entry
- **Endpoint:** `PUT /watchlist/{id}`
- **Description:** Update an existing watchlist entry
- **Parameters:** `id` (Long) - Watchlist entry ID
- **Request Body:** Watchlist object with updated fields
- **Response:** `200 OK` or `400 Bad Request` or `404 Not Found`

### 9. Delete Watchlist Entry
- **Endpoint:** `DELETE /watchlist/{id}`
- **Description:** Delete a watchlist entry
- **Parameters:** `id` (Long) - Watchlist entry ID
- **Response:** `204 No Content` or `404 Not Found`

### 10. Get Watchlist Count
- **Endpoint:** `GET /watchlist/count`
- **Description:** Get the total count of assets in watchlist
- **Response:** `200 OK` with count value

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200  | OK - Request successful |
| 201  | Created - Resource created successfully |
| 204  | No Content - Successful deletion or empty result |
| 400  | Bad Request - Invalid input or validation error |
| 404  | Not Found - Resource not found |
| 500  | Internal Server Error - Server error |

---

## Error Handling

All endpoints handle errors gracefully and return appropriate HTTP status codes.

- **Validation Errors:** 400 Bad Request
- **Resource Not Found:** 404 Not Found
- **Server Errors:** 500 Internal Server Error

---

## CORS Configuration

All endpoints have CORS enabled for cross-origin requests:
- **Allowed Origins:** All (*) 
- **Max Age:** 3600 seconds

---

## Example Usage

### Create an Asset
```bash
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
```

### Get All Assets
```bash
curl -X GET http://localhost:8080/api/assets
```

### Create a Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "asset": { "id": 1 },
    "type": "BUY",
    "quantity": 10.00,
    "price": 150.50,
    "tradeDate": "2026-02-01T12:00:00"
  }'
```

### Update Holding on Buy
```bash
curl -X POST "http://localhost:8080/api/holdings/1/buy?quantity=50&price=150.00"
```

### Add Asset to Watchlist
```bash
curl -X POST "http://localhost:8080/api/watchlist/asset/1?notes=Promising%20stock"
```
