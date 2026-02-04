import React, { useState, useEffect } from 'react';
import * as apiService from '../services/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

/**
 * ChartModal Component - Displays a chart modal for individual holdings
 * Shows historical price data from purchase date to today
 */
const ChartModal = ({ holdingId, symbol, onClose }) => {
  const [chartData, setChartData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchChartData();
  }, [holdingId]);

  const fetchChartData = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await apiService.getHoldingChart(holdingId);
      // Convert price from BigDecimal string or number to proper numeric format
      const processedData = (response.data || []).map(point => ({
        ...point,
        price: typeof point.price === 'string' ? parseFloat(point.price) : parseFloat(point.price)
      }));
      setChartData(processedData);
    } catch (err) {
      console.error('Error fetching chart data:', err);
      setError('Failed to load chart data');
    } finally {
      setLoading(false);
    }
  };

  // Calculate statistics from chart data
  const getChartStats = () => {
    if (chartData.length === 0) return { min: 0, max: 0, current: 0, change: 0, changePercent: 0 };

    const prices = chartData.map(point => parseFloat(point.price));
    const min = Math.min(...prices);
    const max = Math.max(...prices);
    const current = prices[prices.length - 1];
    const initial = prices[0];
    const change = current - initial;
    const changePercent = initial > 0 ? ((change / initial) * 100).toFixed(2) : 0;

    return {
      min: min.toFixed(2),
      max: max.toFixed(2),
      current: current.toFixed(2),
      change: change.toFixed(2),
      changePercent: parseFloat(changePercent).toFixed(2),
    };
  };

  // Determine recommendation based on price movement
  const getRecommendation = () => {
    if (chartData.length < 2) return { recommendation: 'HOLD', confidence: 'Low' };

    const prices = chartData.map(point => parseFloat(point.price));
    const current = prices[prices.length - 1];
    const initial = prices[0];
    const changePercent = initial > 0 ? ((current - initial) / initial) * 100 : 0;

    // Simple recommendation logic (can be enhanced with more sophisticated algorithms)
    if (changePercent > 5) {
      return { recommendation: 'SELL', confidence: 'Medium', reason: 'Price up significantly' };
    } else if (changePercent < -5) {
      return { recommendation: 'BUY', confidence: 'Medium', reason: 'Price down significantly' };
    } else {
      return { recommendation: 'HOLD', confidence: 'Medium', reason: 'Price relatively stable' };
    }
  };

  const stats = getChartStats();
  const recommendation = getRecommendation();

  // Custom tooltip for chart
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{
          backgroundColor: '#fff',
          border: '1px solid #ccc',
          borderRadius: '4px',
          padding: '10px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.15)'
        }}>
          <p style={{ margin: '0 0 5px 0', fontWeight: 'bold' }}>
            {payload[0].payload.date}
          </p>
          <p style={{ margin: '0', color: '#2196F3', fontWeight: 'bold' }}>
            ${parseFloat(payload[0].value).toFixed(2)}
          </p>
        </div>
      );
    }
    return null;
  };

  // Render chart using Recharts
  const renderChart = () => {
    if (chartData.length === 0) {
      return <div style={{ textAlign: 'center', color: '#999', padding: '20px' }}>No data available</div>;
    }

    return (
      <div style={{ marginTop: '10px', marginBottom: '15px' }}>
        <div style={{ fontSize: '12px', color: '#666', marginBottom: '5px' }}>Price History</div>
        <ResponsiveContainer width="100%" height={220}>
          <LineChart
            data={chartData}
            margin={{ top: 5, right: 30, left: 60, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
            <XAxis
              dataKey="date"
              stroke="#999"
              style={{ fontSize: '11px' }}
              tick={{ fill: '#666' }}
            />
            <YAxis
              stroke="#999"
              style={{ fontSize: '11px' }}
              tick={{ fill: '#666' }}
              domain={['dataMin - 5%', 'dataMax + 5%']}
              tickFormatter={(value) => parseFloat(value).toFixed(2)}
              label={{ value: 'Price ($)', angle: -90, position: 'insideLeft', offset: 5 }}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Line
              type="monotone"
              dataKey="price"
              stroke="#2196F3"
              dot={false}
              strokeWidth={2}
              isAnimationActive={true}
              name="Stock Price"
              fill="#2196F3"
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  };

  // Simple ASCII chart rendering
  const renderSimpleChart = () => {
    if (chartData.length === 0) return null;

    return renderChart();
  };

  const isPositive = parseFloat(stats.change) >= 0;

  return (
    <div className="modal active">
      <div className="modal-content" style={{ maxWidth: '900px', maxHeight: '90vh', overflowY: 'auto' }}>
        <div className="modal-header">
          <h2 style={{ margin: '0', fontSize: '18px' }}>{symbol} - Price Chart & Performance</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {loading && (
          <div style={{ padding: '30px', textAlign: 'center' }}>
            <div className="spinner" style={{ display: 'inline-block' }}></div>
            <p>Loading chart data...</p>
          </div>
        )}

        {error && (
          <div style={{ padding: '15px', color: '#d32f2f', backgroundColor: '#ffebee', borderRadius: '4px', margin: '15px' }}>
            {error}
          </div>
        )}

        {!loading && !error && (
          <>
            {/* Chart and Stats */}
            <div style={{ padding: '15px' }}>
              {renderSimpleChart()}

              {/* Stats Grid */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '15px', marginTop: '10px' }}>
                <div style={{ padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                  <div style={{ fontSize: '11px', color: '#666' }}>Current Price</div>
                  <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#333' }}>${stats.current}</div>
                </div>

                <div style={{ padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                  <div style={{ fontSize: '11px', color: '#666' }}>Change</div>
                  <div style={{ fontSize: '16px', fontWeight: 'bold', color: isPositive ? '#4caf50' : '#d32f2f' }}>
                    ${stats.change} ({stats.changePercent}%)
                  </div>
                </div>

                <div style={{ padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                  <div style={{ fontSize: '11px', color: '#666' }}>High</div>
                  <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#333' }}>${stats.max}</div>
                </div>

                <div style={{ padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                  <div style={{ fontSize: '11px', color: '#666' }}>Low</div>
                  <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#333' }}>${stats.min}</div>
                </div>
              </div>

              {/* Recommendation */}
              <div style={{
                padding: '12px',
                backgroundColor: recommendation.recommendation === 'BUY' ? '#c8e6c9' :
                  recommendation.recommendation === 'SELL' ? '#ffcdd2' : '#fff9c4',
                border: `2px solid ${recommendation.recommendation === 'BUY' ? '#4caf50' :
                  recommendation.recommendation === 'SELL' ? '#d32f2f' : '#fbc02d'}`,
                borderRadius: '4px',
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <div style={{ fontSize: '11px', color: '#666', marginBottom: '3px' }}>AI Recommendation</div>
                    <div style={{
                      fontSize: '16px',
                      fontWeight: 'bold',
                      color: recommendation.recommendation === 'BUY' ? '#2e7d32' :
                        recommendation.recommendation === 'SELL' ? '#c62828' : '#f57f17'
                    }}>
                      {recommendation.recommendation}
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '11px', color: '#666', marginBottom: '3px' }}>Confidence</div>
                    <div style={{ fontSize: '12px', fontWeight: 'bold' }}>{recommendation.confidence}</div>
                  </div>
                </div>
                <div style={{ fontSize: '11px', color: '#666', marginTop: '5px' }}>
                  {recommendation.reason}
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={onClose}>
                Close
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ChartModal;
