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
  const [aiRecommendation, setAiRecommendation] = useState(null);
  const [aiLoading, setAiLoading] = useState(true);
  const [aiError, setAiError] = useState('');

  useEffect(() => {
    fetchChartData();
    fetchAIRecommendation();
  }, [holdingId, symbol]);

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

  const fetchAIRecommendation = async () => {
    try {
      setAiLoading(true);
      setAiError('');
      const response = await apiService.getAIRecommendation(symbol);
      setAiRecommendation(response.data);
    } catch (err) {
      console.error('Error fetching AI recommendation:', err);
      setAiError('Failed to load AI recommendation');
    } finally {
      setAiLoading(false);
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
              {aiLoading && (
                <div style={{
                  padding: '12px',
                  backgroundColor: '#f5f5f5',
                  border: '2px solid #ccc',
                  borderRadius: '4px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '11px', color: '#666', marginBottom: '5px' }}>AI Recommendation</div>
                  <div className="spinner" style={{ display: 'inline-block', marginTop: '5px' }}></div>
                  <p style={{ margin: '5px 0 0 0', fontSize: '12px', color: '#666' }}>Loading AI analysis...</p>
                </div>
              )}

              {aiError && (
                <div style={{
                  padding: '12px',
                  backgroundColor: '#ffebee',
                  border: '2px solid #d32f2f',
                  borderRadius: '4px',
                  color: '#d32f2f'
                }}>
                  <div style={{ fontSize: '11px', marginBottom: '5px' }}>AI Recommendation</div>
                  <div style={{ fontSize: '12px' }}>{aiError}</div>
                </div>
              )}

              {!aiLoading && !aiError && aiRecommendation && (
                <div style={{
                  padding: '15px',
                  backgroundColor: '#e3f2fd',
                  border: '2px solid #2196F3',
                  borderRadius: '6px',
                  marginTop: '10px'
                }}>
                  <div style={{ fontSize: '13px', color: '#1976d2', marginBottom: '12px', fontWeight: 'bold', display: 'flex', alignItems: 'center' }}>
                    <span style={{ fontSize: '18px', marginRight: '8px' }}>🤖</span>
                    AI Investment Recommendation
                  </div>
                  <div style={{
                    fontSize: '13px',
                    color: '#333',
                    lineHeight: '1.8',
                    backgroundColor: '#fff',
                    padding: '15px',
                    borderRadius: '6px',
                    maxHeight: '300px',
                    overflowY: 'auto',
                    whiteSpace: 'pre-wrap',
                    wordWrap: 'break-word',
                    fontFamily: 'system-ui, -apple-system, sans-serif'
                  }}>
                    {aiRecommendation.recommendation
                      .split('\n')
                      .map((line, index) => {
                        // Check if line starts with a bullet point or dash
                        if (line.trim().startsWith('•') || line.trim().startsWith('-') || line.trim().startsWith('*')) {
                          return (
                            <div key={index} style={{
                              marginLeft: '15px',
                              marginBottom: '8px',
                              display: 'flex',
                              alignItems: 'flex-start'
                            }}>
                              <span style={{ color: '#2196F3', marginRight: '10px', fontWeight: 'bold', marginTop: '2px' }}>●</span>
                              <span>{line.trim().replace(/^[•\-*]\s*/, '')}</span>
                            </div>
                          );
                        } else if (line.trim().match(/^\d+\./)) {
                          // Handle numbered lists
                          return (
                            <div key={index} style={{
                              marginLeft: '15px',
                              marginBottom: '8px',
                              display: 'flex',
                              alignItems: 'flex-start'
                            }}>
                              <span style={{ color: '#2196F3', marginRight: '10px', fontWeight: 'bold', minWidth: '25px' }}>
                                {line.trim().match(/^\d+/)[0]}.
                              </span>
                              <span>{line.trim().replace(/^\d+\.\s*/, '')}</span>
                            </div>
                          );
                        } else if (line.trim() === '') {
                          // Empty line
                          return <div key={index} style={{ height: '8px' }} />;
                        } else if (line.trim().match(/^[A-Z][^:]*:/) || line.trim().match(/^\*\*[^\*]+\*\*/)) {
                          // Section headers or bold text
                          return (
                            <div key={index} style={{
                              marginTop: '12px',
                              marginBottom: '8px',
                              fontWeight: 'bold',
                              color: '#1976d2',
                              fontSize: '13px'
                            }}>
                              {line.trim().replace(/\*\*/g, '')}
                            </div>
                          );
                        } else {
                          // Regular text
                          return (
                            <div key={index} style={{ marginBottom: '6px' }}>
                              {line}
                            </div>
                          );
                        }
                      })}
                  </div>
                  {aiRecommendation.status === 'error' && (
                    <div style={{ fontSize: '12px', color: '#d32f2f', marginTop: '10px', padding: '8px', backgroundColor: '#ffebee', borderRadius: '4px' }}>
                      ⚠️ Error: {aiRecommendation.message}
                    </div>
                  )}
                </div>
              )}
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
