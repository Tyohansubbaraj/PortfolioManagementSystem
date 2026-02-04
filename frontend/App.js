import React, { useState } from 'react';
import './styles/App.css';
import Dashboard from './src/components/Dashboard';
import Assets from './src/components/Assets';
import Holdings from './src/components/Holdings';
import Transactions from './src/components/Transactions';
import Watchlist from './src/components/Watchlist';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');

  const renderTabContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard />;
      case 'assets':
        return <Assets />;
      case 'holdings':
        return <Holdings />;
      case 'transactions':
        return <Transactions />;
      case 'watchlist':
        return <Watchlist />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="app">
      <nav className="navbar">
        <div className="container">
          <h1>GrowMore</h1>
        </div>
      </nav>

      <div className="container">
        <div className="tabs">
          <button
            className={`tab-button ${activeTab === 'dashboard' ? 'active' : ''}`}
            onClick={() => setActiveTab('dashboard')}
          >
            Dashboard
          </button>
          <button
            className={`tab-button ${activeTab === 'assets' ? 'active' : ''}`}
            onClick={() => setActiveTab('assets')}
          >
            Assets
          </button>
          <button
            className={`tab-button ${activeTab === 'holdings' ? 'active' : ''}`}
            onClick={() => setActiveTab('holdings')}
          >
            Holdings
          </button>
          <button
            className={`tab-button ${activeTab === 'transactions' ? 'active' : ''}`}
            onClick={() => setActiveTab('transactions')}
          >
            Transactions
          </button>
          <button
            className={`tab-button ${activeTab === 'watchlist' ? 'active' : ''}`}
            onClick={() => setActiveTab('watchlist')}
          >
            Watchlist
          </button>
        </div>

        <div className="tab-content">
          {renderTabContent()}
        </div>
      </div>
    </div>
  );
}

export default App;
