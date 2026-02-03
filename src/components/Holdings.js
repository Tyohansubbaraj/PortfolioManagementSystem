import React, { useState, useEffect } from 'react';
import * as apiService from '../services/api';

const Holdings = () => {
  const [holdings, setHoldings] = useState([]);
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showSellModal, setShowSellModal] = useState(false);
  const [sellingHolding, setSellingHolding] = useState(null);
  const [sellFormData, setSellFormData] = useState({
    quantity: '',
  });
  const [stats, setStats] = useState({
    totalInvested: 0,
    currentValue: 0,
    totalPL: 0,
    totalPLPercentage: 0,
  });
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchHoldingsData();
  }, []);

  const fetchHoldingsData = async () => {
    try {
      setLoading(true);
      const [holdingsRes, assetsRes] = await Promise.all([
        apiService.getHoldings(),
        apiService.getAssets(),
      ]);

      const holdingsData = holdingsRes.data || [];
      const assetsData = assetsRes.data || [];

      setHoldings(holdingsData);
      setAssets(assetsData);
      calculateStats(holdingsData, assetsData);
    } catch (error) {
      console.error('Error fetching holdings data:', error);
      setMessage({ type: 'error', text: 'Failed to fetch holdings' });
    } finally {
      setLoading(false);
    }
  };

  const calculateStats = (holdings, assets) => {
    let totalInvested = 0;
    let currentValue = 0;

    holdings.forEach((holding) => {
      const invested = parseFloat(holding.totalQuantity) * parseFloat(holding.avgBuyPrice);
      totalInvested += invested;

      const asset = assets.find((a) => a.id === holding.asset.id);
      if (asset) {
        const current = parseFloat(holding.totalQuantity) * parseFloat(asset.currentPrice);
        currentValue += current;
      }
    });

    const totalPL = currentValue - totalInvested;
    const totalPLPercentage = totalInvested > 0 ? (totalPL / totalInvested) * 100 : 0;

    setStats({
      totalInvested: totalInvested.toFixed(2),
      currentValue: currentValue.toFixed(2),
      totalPL: totalPL.toFixed(2),
      totalPLPercentage: totalPLPercentage.toFixed(2),
    });
  };

  const handleSellInputChange = (e) => {
    const { name, value } = e.target;
    setSellFormData({ ...sellFormData, [name]: value });
  };

  const handleSellAsset = (holding) => {
    setSellingHolding(holding);
    setSellFormData({ quantity: '' });
    setShowSellModal(true);
  };

  const handleConfirmSell = async () => {
    if (!sellFormData.quantity) {
      setMessage({ type: 'error', text: 'Please enter quantity' });
      return;
    }

    const qtyNum = parseFloat(sellFormData.quantity);
    if (qtyNum <= 0 || qtyNum > parseFloat(sellingHolding.totalQuantity)) {
      setMessage({ type: 'error', text: 'Invalid quantity' });
      return;
    }

    const asset = assets.find((a) => a.id === sellingHolding.asset.id);
    if (!asset) return;

    try {
      // Update holding
      await apiService.sellHolding(sellingHolding.id, sellFormData.quantity);

      // Create transaction
      await apiService.createTransaction({
        asset: { id: asset.id },
        type: 'SELL',
        quantity: qtyNum,
        price: parseFloat(asset.currentPrice),
        tradeDate: new Date().toISOString(),
      });

      setMessage({ type: 'success', text: `Sold ${sellFormData.quantity} shares of ${asset.symbol}` });
      setShowSellModal(false);
      setSellingHolding(null);
      fetchHoldingsData();
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      console.error('Error selling asset:', error);
      setMessage({ type: 'error', text: 'Failed to sell asset' });
    }
  };

  const getHoldingDetails = (holding) => {
    const asset = assets.find((a) => a.id === holding.asset.id);
    if (!asset) return null;

    const invested = parseFloat(holding.totalQuantity) * parseFloat(holding.avgBuyPrice);
    const current = parseFloat(holding.totalQuantity) * parseFloat(asset.currentPrice);
    const pl = current - invested;
    const plPercentage = invested > 0 ? ((pl / invested) * 100).toFixed(2) : 0;

    return {
      symbol: asset.symbol,
      name: asset.name,
      quantity: parseFloat(holding.totalQuantity),
      avgBuyPrice: parseFloat(holding.avgBuyPrice),
      currentPrice: parseFloat(asset.currentPrice),
      pl: pl.toFixed(2),
      plPercentage: plPercentage,
    };
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <p>Loading holdings...</p>
      </div>
    );
  }

  return (
    <div>
      {message.text && <div className={message.type}>{message.text}</div>}

      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Invested</h3>
          <div className="value">${stats.totalInvested}</div>
          <div className="subtext">Cost basis</div>
        </div>

        <div className="stat-card">
          <h3>Current Value</h3>
          <div className="value">${stats.currentValue}</div>
          <div className="subtext">Live prices</div>
        </div>

        <div className="stat-card">
          <h3>Total P&L</h3>
          <div className={`value ${parseFloat(stats.totalPL) >= 0 ? 'positive' : 'negative'}`}>
            ${stats.totalPL}
          </div>
          <div className={`subtext ${parseFloat(stats.totalPL) >= 0 ? 'positive' : 'negative'}`}>
            {stats.totalPLPercentage}%
          </div>
        </div>

        <div className="stat-card">
          <h3>Holdings</h3>
          <div className="value">{holdings.length}</div>
          <div className="subtext">Total assets held</div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: '20px', fontSize: '18px', fontWeight: '600' }}>Holdings Details</h3>
        <div style={{ overflowX: 'auto' }}>
          <table>
            <thead>
              <tr>
                <th>Symbol</th>
                <th>Name</th>
                <th>Quantity</th>
                <th>Avg Buy Price</th>
                <th>Current Price</th>
                <th>P&L</th>
                <th>% Change</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {holdings.map((holding) => {
                const details = getHoldingDetails(holding);
                if (!details) return null;

                return (
                  <tr key={holding.id}>
                    <td className="ticker">{details.symbol}</td>
                    <td>{details.name}</td>
                    <td>{details.quantity.toFixed(4)}</td>
                    <td>${details.avgBuyPrice.toFixed(2)}</td>
                    <td>${details.currentPrice.toFixed(2)}</td>
                    <td className={parseFloat(details.pl) >= 0 ? 'positive' : 'negative'}>
                      ${details.pl}
                    </td>
                    <td className={parseFloat(details.plPercentage) >= 0 ? 'positive' : 'negative'}>
                      {details.plPercentage}%
                    </td>
                    <td>
                      <div className="actions">
                        <button
                          className="btn btn-danger"
                          onClick={() => handleSellAsset(holding)}
                        >
                          Sell
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {showSellModal && sellingHolding && (
        <div className="modal active">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Sell {assets.find((a) => a.id === sellingHolding.asset.id)?.symbol}</h2>
              <button className="modal-close" onClick={() => setShowSellModal(false)}>×</button>
            </div>

            <div className="form-group">
              <label>Asset</label>
              <div style={{ padding: '8px 10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                <strong>{assets.find((a) => a.id === sellingHolding.asset.id)?.symbol}</strong> - {assets.find((a) => a.id === sellingHolding.asset.id)?.name}
              </div>
            </div>

            <div className="form-group">
              <label>Current Price</label>
              <div style={{ padding: '8px 10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                ${assets.find((a) => a.id === sellingHolding.asset.id) ? parseFloat(assets.find((a) => a.id === sellingHolding.asset.id).currentPrice).toFixed(2) : '0.00'}
              </div>
            </div>

            <div className="form-group">
              <label>Available Quantity: {parseFloat(sellingHolding.totalQuantity).toFixed(4)}</label>
              <input
                type="number"
                name="quantity"
                value={sellFormData.quantity}
                onChange={handleSellInputChange}
                placeholder="Enter quantity to sell"
                step="0.0001"
                min="0"
                max={sellingHolding.totalQuantity}
              />
            </div>

            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setShowSellModal(false)}>
                Cancel
              </button>
              <button className="btn btn-danger" onClick={handleConfirmSell}>
                Sell
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Holdings;
