import React, { useState, useEffect } from 'react';
import * as apiService from '../services/api';

const Assets = () => {
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('symbol');
  const [filterType, setFilterType] = useState('');
  const [filterSector, setFilterSector] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [selectedAsset, setSelectedAsset] = useState(null);
  const [buyingAsset, setBuyingAsset] = useState(null);
  const [watchlist, setWatchlist] = useState(new Set());
  const [formData, setFormData] = useState({
    symbol: '',
    name: '',
    type: '',
    sector: '',
    currentPrice: '',
  });
  const [buyFormData, setBuyFormData] = useState({
    quantity: '',
  });
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchAssets();
    fetchWatchlistStatus();
  }, []);

  const fetchAssets = async () => {
    try {
      setLoading(true);
      const response = await apiService.getAssets();
      setAssets(response.data || []);
    } catch (error) {
      console.error('Error fetching assets:', error);
      setMessage({ type: 'error', text: 'Failed to fetch assets' });
    } finally {
      setLoading(false);
    }
  };

  const fetchWatchlistStatus = async () => {
    try {
      const response = await apiService.getWatchlist();
      const watchlistAssets = response.data ? response.data.map((item) => item.asset.id) : [];
      setWatchlist(new Set(watchlistAssets));
    } catch (error) {
      console.error('Error fetching watchlist:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleBuyInputChange = (e) => {
    const { name, value } = e.target;
    setBuyFormData({ ...buyFormData, [name]: value });
  };

  const handleCreateAsset = async () => {
    if (!formData.symbol || !formData.name || !formData.type || !formData.sector || !formData.currentPrice) {
      setMessage({ type: 'error', text: 'Please fill all fields' });
      return;
    }

    try {
      await apiService.createAsset({
        ...formData,
        currentPrice: parseFloat(formData.currentPrice),
        lastUpdated: new Date().toISOString(),
      });
      setMessage({ type: 'success', text: 'Asset created successfully!' });
      fetchAssets();
      setShowModal(false);
      setFormData({ symbol: '', name: '', type: '', sector: '', currentPrice: '' });
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      console.error('Error creating asset:', error);
      setMessage({ type: 'error', text: 'Failed to create asset' });
    }
  };

  const handleBuyAsset = (asset) => {
    setBuyingAsset(asset);
    setBuyFormData({ quantity: '' });
    setShowBuyModal(true);
  };

  const handleConfirmBuy = async () => {
    if (!buyFormData.quantity) {
      setMessage({ type: 'error', text: 'Please enter quantity' });
      return;
    }

    try {
      const quantity = parseFloat(buyFormData.quantity);
      const price = parseFloat(buyingAsset.currentPrice);

      // Check if holding exists
      let holdingExists = false;
      let existingHolding = null;

      try {
        const holdingRes = await apiService.getHoldingByAssetId(buyingAsset.id);
        if (holdingRes.status === 200 && holdingRes.data) {
          holdingExists = true;
          existingHolding = holdingRes.data;
        }
      } catch (err) {
        // No holding exists, which is fine - we'll create a new one
        holdingExists = false;
      }

      if (holdingExists && existingHolding) {
        // Update existing holding
        await apiService.buyHolding(existingHolding.id, quantity, price);
      } else {
        // Create new holding
        await apiService.createHolding({
          asset: { id: buyingAsset.id },
          totalQuantity: quantity,
          avgBuyPrice: price,
        });
      }

      // Create transaction
      await apiService.createTransaction({
        asset: { id: buyingAsset.id },
        type: 'BUY',
        quantity: quantity,
        price: price,
        tradeDate: new Date().toISOString(),
      });

      setMessage({ type: 'success', text: `Bought ${quantity} shares of ${buyingAsset.symbol}` });
      setShowBuyModal(false);
      setBuyingAsset(null);
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      console.error('Error buying asset:', error);
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to buy asset' });
    }
  };

  const handleToggleWatchlist = async (asset) => {
    try {
      if (watchlist.has(asset.id)) {
        await apiService.removeFromWatchlist(asset.id);
        setWatchlist((prev) => {
          const newSet = new Set(prev);
          newSet.delete(asset.id);
          return newSet;
        });
        setMessage({ type: 'success', text: `${asset.symbol} removed from watchlist` });
      } else {
        await apiService.addToWatchlist(asset.id);
        setWatchlist((prev) => new Set([...prev, asset.id]));
        setMessage({ type: 'success', text: `${asset.symbol} added to watchlist` });
      }
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      console.error('Error updating watchlist:', error);
      setMessage({ type: 'error', text: 'Failed to update watchlist' });
    }
  };

  const getFilteredAndSortedAssets = () => {
    let filtered = assets.filter((asset) => {
      const matchSearch =
        asset.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
        asset.name.toLowerCase().includes(searchTerm.toLowerCase());
      const matchType = !filterType || asset.type === filterType;
      const matchSector = !filterSector || asset.sector === filterSector;
      return matchSearch && matchType && matchSector;
    });

    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'symbol':
          return a.symbol.localeCompare(b.symbol);
        case 'name':
          return a.name.localeCompare(b.name);
        case 'price-asc':
          return parseFloat(a.currentPrice) - parseFloat(b.currentPrice);
        case 'price-desc':
          return parseFloat(b.currentPrice) - parseFloat(a.currentPrice);
        default:
          return 0;
      }
    });

    return filtered;
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <p>Loading assets...</p>
      </div>
    );
  }

  const filteredAssets = getFilteredAndSortedAssets();
  const types = [...new Set(assets.map((a) => a.type))];
  const sectors = [...new Set(assets.map((a) => a.sector))];

  return (
    <div>
      {message.text && <div className={message.type}>{message.text}</div>}

      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>Assets</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            + Add Asset
          </button>
        </div>

        <div className="search-bar">
          <input
            type="text"
            placeholder="Search by symbol or name..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
            <option value="symbol">Sort by Symbol</option>
            <option value="name">Sort by Name</option>
            <option value="price-asc">Price (Low to High)</option>
            <option value="price-desc">Price (High to Low)</option>
          </select>
          <select value={filterType} onChange={(e) => setFilterType(e.target.value)}>
            <option value="">All Types</option>
            {types.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
          <select value={filterSector} onChange={(e) => setFilterSector(e.target.value)}>
            <option value="">All Sectors</option>
            {sectors.map((sector) => (
              <option key={sector} value={sector}>
                {sector}
              </option>
            ))}
          </select>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table>
            <thead>
              <tr>
                <th>Asset</th>
                <th>Price</th>
                <th>Type</th>
                <th>Sector</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredAssets.map((asset) => (
                <tr key={asset.id}>
                  <td>
                    <div className="ticker">{asset.symbol}</div>
                    <div className="name">{asset.name}</div>
                  </td>
                  <td>${parseFloat(asset.currentPrice).toFixed(2)}</td>
                  <td>{asset.type}</td>
                  <td>{asset.sector}</td>
                  <td>
                    <div className="actions">
                      <button
                        className="btn btn-primary"
                        onClick={() => handleBuyAsset(asset)}
                      >
                        Add
                      </button>
                      <button
                        className={`btn ${watchlist.has(asset.id) ? 'btn-success' : 'btn-secondary'}`}
                        onClick={() => handleToggleWatchlist(asset)}
                      >
                        {watchlist.has(asset.id) ? '✓ Watchlist' : 'Watchlist'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="modal active">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Add New Asset</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>

            <div className="form-group">
              <label>Symbol</label>
              <input
                type="text"
                name="symbol"
                value={formData.symbol}
                onChange={handleInputChange}
                placeholder="e.g., AAPL"
              />
            </div>

            <div className="form-group">
              <label>Name</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                placeholder="e.g., Apple Inc."
              />
            </div>

            <div className="form-group">
              <label>Type</label>
              <select name="type" value={formData.type} onChange={handleInputChange}>
                <option value="">Select Type</option>
                <option value="Stock">Stock</option>
                <option value="Crypto">Crypto</option>
                <option value="Bond">Bond</option>
                <option value="ETF">ETF</option>
                <option value="Mutual Fund">Mutual Fund</option>
              </select>
            </div>

            <div className="form-group">
              <label>Sector</label>
              <input
                type="text"
                name="sector"
                value={formData.sector}
                onChange={handleInputChange}
                placeholder="e.g., Technology"
              />
            </div>

            <div className="form-group">
              <label>Current Price</label>
              <input
                type="number"
                name="currentPrice"
                value={formData.currentPrice}
                onChange={handleInputChange}
                placeholder="e.g., 150.50"
                step="0.01"
              />
            </div>

            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setShowModal(false)}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleCreateAsset}>
                Create Asset
              </button>
            </div>
          </div>
        </div>
      )}

      {showBuyModal && buyingAsset && (
      // add purchase date so that we can fetch price on that day
        <div className="modal active">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Buy {buyingAsset.symbol}</h2>
              <button className="modal-close" onClick={() => setShowBuyModal(false)}>×</button>
            </div>

            <div className="form-group">
              <label>Asset</label>
              <div style={{ padding: '8px 10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                <strong>{buyingAsset.symbol}</strong> - {buyingAsset.name}
              </div>
            </div>

            <div className="form-group">
              <label>Current Price</label>
              <div style={{ padding: '8px 10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                ${parseFloat(buyingAsset.currentPrice).toFixed(2)}
              </div>
            </div>

            <div className="form-group">
              <label>Quantity</label>
              <input
                type="number"
                name="quantity"
                value={buyFormData.quantity}
                onChange={handleBuyInputChange}
                placeholder="Enter quantity"
                step="0.01"
                min="0"
              />
            </div>

            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setShowBuyModal(false)}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleConfirmBuy}>
                Buy
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};



export default Assets;
