import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
export const downloadExcel = () => api.get('/export/excel', {
    responseType: 'blob'
});
// Asset API calls
export const getAssets = () => api.get('/assets');
export const getAssetById = (id) => api.get(`/assets/${id}`);
export const getAssetBySymbol = (symbol) => api.get(`/assets/symbol/${symbol}`);
export const getAssetsByType = (type) => api.get(`/assets/type/${type}`);
export const getAssetsBySector = (sector) => api.get(`/assets/sector/${sector}`);
export const createAsset = (asset) => api.post('/assets', asset);
export const updateAsset = (id, asset) => api.put(`/assets/${id}`, asset);
export const deleteAsset = (id) => api.delete(`/assets/${id}`);
export const checkAssetExists = (symbol) => api.get(`/assets/exists/${symbol}`);

// Holding API calls
export const getHoldings = () => api.get('/holdings');
export const getHoldingById = (id) => api.get(`/holdings/${id}`);
export const getHoldingByAssetId = (assetId) => api.get(`/holdings/asset/${assetId}`);
export const createHolding = (holding) => api.post('/holdings', holding);
export const updateHolding = (id, holding) => api.put(`/holdings/${id}`, holding);
export const buyHolding = (id, quantity, price) =>
  api.post(`/holdings/${id}/buy?quantity=${quantity}&price=${price}`);
export const sellHolding = (id, quantity) =>
  api.post(`/holdings/${id}/sell?quantity=${quantity}`);
export const deleteHolding = (id) => api.delete(`/holdings/${id}`);

// Transaction API calls
export const getTransactions = () => api.get('/transactions');
export const getTransactionById = (id) => api.get(`/transactions/${id}`);
export const getTransactionsByAssetId = (assetId) => api.get(`/transactions/asset/${assetId}`);
export const getTransactionsByType = (type) => api.get(`/transactions/type/${type}`);
export const getTransactionsByDateRange = (startDate, endDate) =>
  api.get('/transactions/daterange', {
    params: {
      startDate,
      endDate,
    },
  });
export const createTransaction = (transaction) => api.post('/transactions', transaction);
export const updateTransaction = (id, transaction) => api.put(`/transactions/${id}`, transaction);
export const deleteTransaction = (id) => api.delete(`/transactions/${id}`);

// Watchlist API calls
export const getWatchlist = () => api.get('/watchlist');
export const getWatchlistById = (id) => api.get(`/watchlist/${id}`);
export const getWatchlistByAssetId = (assetId) => api.get(`/watchlist/asset/${assetId}`);
export const addToWatchlist = (assetId, notes = '') =>
  api.post(`/watchlist/asset/${assetId}`, {}, {
    params: { notes },
  });
export const removeFromWatchlist = (assetId) => api.delete(`/watchlist/asset/${assetId}`);
export const updateWatchlistEntry = (id, entry) => api.put(`/watchlist/${id}`, entry);
export const deleteWatchlistEntry = (id) => api.delete(`/watchlist/${id}`);
export const getWatchlistCount = () => api.get('/watchlist/count');
export const checkInWatchlist = (assetId) => api.get(`/watchlist/asset/${assetId}/exists`);
// Add these to your existing api.js file



// Alerts API calls
export const getAlerts = () => api.get('/alerts');
export const getAlert = (id) => api.get(`/alerts/${id}`);
export const createAlert = (alert) => api.post('/alerts', alert);
export const updateAlert = (id, alert) => api.put(`/alerts/${id}`, alert);
export const deleteAlert = (id) => api.delete(`/alerts/${id}`);
export const checkAlerts = () => api.get('/alerts/check');

// Risk & Performance API calls
export const getRiskAnalysis = (portfolioId) => api.get(`/api/analysis/risk/${portfolioId}`);
export const getPerformance = (portfolioId) => api.get(`/api/analysis/performance/${portfolioId}`);

// Multi-Portfolio API calls
export const getPortfolios = () => api.get('/api/portfolios');
export default api;
