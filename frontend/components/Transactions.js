import React, { useState, useEffect } from 'react';
import * as apiService from '../services/api';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterType, setFilterType] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchTransactions();
    fetchAssets();
  }, []);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const response = await apiService.getTransactions();
      setTransactions(response.data || []);
    } catch (error) {
      console.error('Error fetching transactions:', error);
      setMessage({ type: 'error', text: 'Failed to fetch transactions' });
    } finally {
      setLoading(false);
    }
  };

  const fetchAssets = async () => {
    try {
      const response = await apiService.getAssets();
      setAssets(response.data || []);
    } catch (error) {
      console.error('Error fetching assets:', error);
    }
  };

  const handleFilterByDateRange = async () => {
    if (!startDate || !endDate) {
      setMessage({ type: 'error', text: 'Please select both start and end dates' });
      return;
    }

    try {
      setLoading(true);
      const response = await apiService.getTransactionsByDateRange(
        new Date(startDate).toISOString(),
        new Date(endDate).toISOString()
      );
      setTransactions(response.data || []);
      setMessage({ type: 'success', text: 'Transactions filtered by date range' });
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      console.error('Error filtering transactions:', error);
      setMessage({ type: 'error', text: 'Failed to filter transactions' });
    } finally {
      setLoading(false);
    }
  };

  const handleFilterByType = async (type) => {
    try {
      setLoading(true);
      setFilterType(type);
      if (type === '') {
        fetchTransactions();
      } else {
        const response = await apiService.getTransactionsByType(type);
        setTransactions(response.data || []);
      }
    } catch (error) {
      console.error('Error filtering transactions:', error);
      setMessage({ type: 'error', text: 'Failed to filter transactions' });
    } finally {
      setLoading(false);
    }
  };

  const handleClearFilters = () => {
    setFilterType('');
    setStartDate('');
    setEndDate('');
    fetchTransactions();
  };

  const getAssetName = (assetId) => {
    const asset = assets.find((a) => a.id === assetId);
    return asset ? asset.name : 'Unknown';
  };

  const getAssetSymbol = (assetId) => {
    const asset = assets.find((a) => a.id === assetId);
    return asset ? asset.symbol : 'N/A';
  };

  const getFilteredTransactions = () => {
    return transactions.sort((a, b) => {
      return new Date(b.tradeDate) - new Date(a.tradeDate);
    });
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <p>Loading transactions...</p>
      </div>
    );
  }

  const filteredTransactions = getFilteredTransactions();

  return (
    <div>
      {message.text && <div className={message.type}>{message.text}</div>}

      <div className="card">
        <h3 style={{ marginBottom: '20px', fontSize: '18px', fontWeight: '600' }}>Transaction Filters</h3>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '10px', marginBottom: '20px' }}>
          <button
            className={`btn ${filterType === '' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => handleFilterByType('')}
          >
            All Transactions
          </button>
          <button
            className={`btn ${filterType === 'BUY' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => handleFilterByType('BUY')}
          >
            Buy
          </button>
          <button
            className={`btn ${filterType === 'SELL' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => handleFilterByType('SELL')}
          >
            Sell
          </button>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '10px', marginBottom: '20px' }}>
          <div className="form-group" style={{ margin: 0 }}>
            <label>Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>
          <div className="form-group" style={{ margin: 0 }}>
            <label>End Date</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>
          <div style={{ display: 'flex', gap: '10px', alignItems: 'flex-end' }}>
            <button className="btn btn-primary" onClick={handleFilterByDateRange}>
              Filter by Date
            </button>
            <button className="btn btn-secondary" onClick={handleClearFilters}>
              Clear Filters
            </button>
          </div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: '20px', fontSize: '18px', fontWeight: '600' }}>
          Transactions ({filteredTransactions.length})
        </h3>
        <div style={{ overflowX: 'auto' }}>
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Symbol</th>
                <th>Asset Name</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Total Amount</th>
              </tr>
            </thead>
            <tbody>
              {filteredTransactions.length > 0 ? (
                filteredTransactions.map((transaction) => {
                  const totalAmount = (parseFloat(transaction.quantity) * parseFloat(transaction.price)).toFixed(2);
                  const date = new Date(transaction.tradeDate).toLocaleDateString();

                  return (
                    <tr key={transaction.id}>
                      <td>{date}</td>
                      <td>
                        <span
                          style={{
                            padding: '4px 8px',
                            borderRadius: '4px',
                            backgroundColor: transaction.type === 'BUY' ? '#d1fae5' : '#fee2e2',
                            color: transaction.type === 'BUY' ? '#059669' : '#dc2626',
                            fontWeight: 500,
                          }}
                        >
                          {transaction.type}
                        </span>
                      </td>
                      <td className="ticker">{getAssetSymbol(transaction.asset.id)}</td>
                      <td>{getAssetName(transaction.asset.id)}</td>
                      <td>{parseFloat(transaction.quantity).toFixed(4)}</td>
                      <td>${parseFloat(transaction.price).toFixed(2)}</td>
                      <td className={transaction.type === 'BUY' ? 'negative' : 'positive'}>
                        ${totalAmount}
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
                    No transactions found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Transactions;
