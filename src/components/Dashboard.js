import React, { useState, useEffect } from 'react';
import {
  PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import * as apiService from '../services/api';

const Dashboard = () => {
  const [holdings, setHoldings] = useState([]);
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalInvested: 0,
    currentValue: 0,
    totalPL: 0,
    totalPLPercentage: 0,
    assetCount: 0,
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
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
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const calculateStats = (holdings, assets) => {
    let totalInvested = 0;
    let currentValue = 0;

    holdings.forEach((holding) => {
      const amount = parseFloat(holding.totalQuantity) * parseFloat(holding.avgBuyPrice);
      totalInvested += amount;

      const asset = assets.find((a) => a.id === holding.asset.id);
      if (asset) {
        const currentAmount = parseFloat(holding.totalQuantity) * parseFloat(asset.currentPrice);
        currentValue += currentAmount;
      }
    });

    const totalPL = currentValue - totalInvested;
    const totalPLPercentage = totalInvested > 0 ? (totalPL / totalInvested) * 100 : 0;

    setStats({
      totalInvested: totalInvested.toFixed(2),
      currentValue: currentValue.toFixed(2),
      totalPL: totalPL.toFixed(2),
      totalPLPercentage: totalPLPercentage.toFixed(2),
      assetCount: holdings.length,
    });
  };

  const getAssetAllocationData = () => {
    const typeMap = {};
    holdings.forEach((holding) => {
      const asset = assets.find((a) => a.id === holding.asset.id);
      if (asset) {
        const amount = parseFloat(holding.totalQuantity) * parseFloat(asset.currentPrice);
        typeMap[asset.type] = (typeMap[asset.type] || 0) + amount;
      }
    });

    return Object.entries(typeMap).map(([name, value]) => ({
      name,
      value: parseFloat(value.toFixed(2)),
    }));
  };

  const getReturnsData = () => {
    return holdings
      .slice(0, 5)
      .map((holding) => {
        const asset = assets.find((a) => a.id === holding.asset.id);
        if (asset) {
          const invested = parseFloat(holding.totalQuantity) * parseFloat(holding.avgBuyPrice);
          const current = parseFloat(holding.totalQuantity) * parseFloat(asset.currentPrice);
          const pl = current - invested;
          return {
            name: asset.symbol,
            pl: parseFloat(pl.toFixed(2)),
            return: parseFloat(((pl / invested) * 100).toFixed(2)),
          };
        }
        return null;
      })
      .filter(Boolean);
  };

  const getTopHoldings = () => {
    return holdings
      .map((holding) => {
        const asset = assets.find((a) => a.id === holding.asset.id);
        if (asset) {
          const invested = parseFloat(holding.totalQuantity) * parseFloat(holding.avgBuyPrice);
          const current = parseFloat(holding.totalQuantity) * parseFloat(asset.currentPrice);
          const pl = current - invested;
          return {
            symbol: asset.symbol,
            name: asset.name,
            quantity: parseFloat(holding.totalQuantity),
            currentPrice: parseFloat(asset.currentPrice),
            currentValue: current,
            pl,
            plPercentage: invested > 0 ? ((pl / invested) * 100).toFixed(2) : 0,
          };
        }
        return null;
      })
      .filter(Boolean)
      .sort((a, b) => b.currentValue - a.currentValue)
      .slice(0, 5);
  };

  const COLORS = ['#667eea', '#764ba2', '#f093fb', '#4facfe', '#00f2fe', '#43e97b', '#fa709a'];

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  const allocationData = getAssetAllocationData();
  const returnsData = getReturnsData();
  const topHoldings = getTopHoldings();

  return (
    <div>
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Amount Invested</h3>
          <div className="value">${stats.totalInvested}</div>
          <div className="subtext">{holdings.length} assets</div>
        </div>

        <div className="stat-card">
          <h3>Current Value</h3>
          <div className="value">${stats.currentValue}</div>
          <div className="subtext">Live market prices</div>
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
          <h3>Total Assets</h3>
          <div className="value">{stats.assetCount}</div>
          <div className="subtext">In portfolio</div>
        </div>
      </div>

      {allocationData.length > 0 && (
        <div className="charts-container">
          <div className="chart-card">
            <h3>Asset Allocation</h3>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={allocationData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, value }) => `${name}: ${((value / allocationData.reduce((sum, item) => sum + item.value, 0)) * 100).toFixed(1)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {allocationData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {returnsData.length > 0 && (
            <div className="chart-card">
              <h3>Top 5 Asset Returns</h3>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={returnsData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e9ecef" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip
                    formatter={(value) => [`${value.toFixed(2)}%`, 'Return %']}
                    contentStyle={{ backgroundColor: '#fff', border: '1px solid #ccc' }}
                  />
                  <Bar dataKey="return" fill="#667eea" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>
      )}

      {topHoldings.length > 0 && (
        <div className="card">
          <h3 style={{ marginBottom: '20px', fontSize: '18px', fontWeight: '600' }}>Top Holdings</h3>
          <div style={{ overflowX: 'auto' }}>
            <table>
              <thead>
                <tr>
                  <th>Symbol</th>
                  <th>Name</th>
                  <th>Quantity</th>
                  <th>Current Price</th>
                  <th>Current Value</th>
                  <th>P&L</th>
                  <th>P&L %</th>
                </tr>
              </thead>
              <tbody>
                {topHoldings.map((holding) => (
                  <tr key={holding.symbol}>
                    <td className="ticker">{holding.symbol}</td>
                    <td>{holding.name}</td>
                    <td>{holding.quantity.toFixed(4)}</td>
                    <td>${holding.currentPrice.toFixed(2)}</td>
                    <td>${holding.currentValue.toFixed(2)}</td>
                    <td className={holding.pl >= 0 ? 'positive' : 'negative'}>
                      ${holding.pl.toFixed(2)}
                    </td>
                    <td className={holding.plPercentage >= 0 ? 'positive' : 'negative'}>
                      {holding.plPercentage}%
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};


export default Dashboard;
