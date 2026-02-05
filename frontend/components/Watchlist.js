import React, { useState, useEffect } from 'react';
import * as apiService from '../services/api';

const Watchlist = () => {
  const [watchlist, setWatchlist] = useState([]);
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [buyingAsset, setBuyingAsset] = useState(null);
  const [buyFormData, setBuyFormData] = useState({
    quantity: '',
  });
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchWatchlistData();
  }, []);

  const fetchWatchlistData = async () => {
    try {
      setLoading(true);
      const [watchlistRes, assetsRes] = await Promise.all([
        apiService.getWatchlist(),
        apiService.getAssets(),
      ]);

      setWatchlist(watchlistRes.data || []);
      setAssets(assetsRes.data || []);
    } catch (error) {
      console.error('Error fetching watchlist:', error);
      setMessage({ type: 'error', text: 'Failed to fetch watchlist' });
    } finally {
      setLoading(false);
    }
  };

  const handleBuyInputChange = (e) => {
    const { name, value } = e.target;
    setBuyFormData({ ...buyFormData, [name]: value });
  };

  const handleRemoveFromWatchlist = async (assetId) => {
    if (!window.confirm('Are you sure you want to remove this asset from watchlist?')) {
      return;
    }

    try {
      await apiService.removeFromWatchlist(assetId);
      setMessage({ type: 'success', text: 'Asset removed from watchlist' });
      fetchWatchlistData();
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      console.error('Error removing from watchlist:', error);
      setMessage({ type: 'error', text: 'Failed to remove from watchlist' });
    }
  };

  const handleBuyFromWatchlist = (assetId) => {
    const asset = assets.find((a) => a.id === assetId);
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

  const getAssetDetails = (assetId) => {
    return assets.find((a) => a.id === assetId);
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <p>Loading watchlist...</p>
      </div>
    );
  }

  return (
    <div>
      {message.text && <div className={message.type}>{message.text}</div>}

      <div className="card">
        <h3 style={{ marginBottom: '20px', fontSize: '18px', fontWeight: '600' }}>
          Watchlist ({watchlist.length} assets)
        </h3>

        {watchlist.length > 0 ? (
          <div style={{ overflowX: 'auto' }}>
            <table>
              <thead>
                <tr>
                  <th>Symbol</th>
                  <th>Name</th>
                  <th>Type</th>
                  <th>Sector</th>
                  <th>Current Price</th>
                  <th>Last Updated</th>
                  <th>Notes</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {watchlist.map((entry) => {
                  const asset = getAssetDetails(entry.asset.id);
                  if (!asset) return null;

                  const lastUpdated = new Date(asset.lastUpdated).toLocaleDateString();

                  return (
                    <tr key={entry.id}>
                      <td className="ticker">{asset.symbol}</td>
                      <td>{asset.name}</td>
                      <td>{asset.type}</td>
                      <td>{asset.sector}</td>
                      <td>${parseFloat(asset.currentPrice).toFixed(2)}</td>
                      <td>{lastUpdated}</td>
                      <td>{entry.notes || '-'}</td>
                      <td>
                        <div className="actions">
                          <button
                            className="btn btn-primary"
                            onClick={() => handleBuyFromWatchlist(entry.asset.id)}
                          >
                            Add
                          </button>
                          <button
                            className="btn btn-danger"
                            onClick={() => handleRemoveFromWatchlist(entry.asset.id)}
                          >
                            Remove
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        ) : (
          <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
            <p style={{ fontSize: '16px', marginBottom: '10px' }}>No assets in watchlist</p>
            <p>Go to Assets tab and click "Watchlist" to add assets</p>
          </div>
        )}
      </div>

      {showBuyModal && buyingAsset && (
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
                Add
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Watchlist;
