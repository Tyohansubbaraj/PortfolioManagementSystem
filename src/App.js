import React, { useState, useEffect } from 'react';
import './styles/App.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Components
import Dashboard from './components/Dashboard';
import Assets from './components/Assets';
import Holdings from './components/Holdings';
import Transactions from './components/Transactions';
import Watchlist from './components/Watchlist';
import RiskAnalysis from './components/RiskAnalysis';
import TaxReports from './components/TaxReports';
import Alerts from './components/Alerts';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');

  // Real-time Alert System
  useEffect(() => {
    const eventSource = new EventSource("http://localhost:8080/api/alerts/stream");
    eventSource.onmessage = (event) => {
      toast.info(`🔔 Alert: ${event.data}`, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        theme: "light",
      });
    };
    return () => eventSource.close();
  }, []);

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard': return <Dashboard />;
      case 'holdings': return <Holdings />;
      case 'risk': return <RiskAnalysis />;
      case 'reports': return <TaxReports />;
      case 'assets': return <Assets />;
      case 'transactions': return <Transactions />;
      case 'watchlist': return <Watchlist />;
      case 'alerts': return <Alerts />;
      default: return <Dashboard />;
    }
  };

  return (
    <div className="app">
      <ToastContainer />

      <nav className="navbar">
        <h1>GrowMore</h1>
      </nav>

      <div className="tabs">
        <button className={`tab-button ${activeTab === 'dashboard' ? 'active' : ''}`} onClick={() => setActiveTab('dashboard')}>Overview</button>
        <button className={`tab-button ${activeTab === 'holdings' ? 'active' : ''}`} onClick={() => setActiveTab('holdings')}>Inventory</button>
        <button className={`tab-button ${activeTab === 'risk' ? 'active' : ''}`} onClick={() => setActiveTab('risk')}>Risk Engine</button>
        <button className={`tab-button ${activeTab === 'reports' ? 'active' : ''}`} onClick={() => setActiveTab('reports')}>Tax Center</button>
        <button className={`tab-button ${activeTab === 'assets' ? 'active' : ''}`} onClick={() => setActiveTab('assets')}>Market</button>
        <button className={`tab-button ${activeTab === 'transactions' ? 'active' : ''}`} onClick={() => setActiveTab('transactions')}>History</button>
        <button className={`tab-button ${activeTab === 'watchlist' ? 'active' : ''}`} onClick={() => setActiveTab('watchlist')}>Watchlist</button>
        <button className={`tab-button ${activeTab === 'alerts' ? 'active' : ''}`} onClick={() => setActiveTab('alerts')}>Alerts</button>
      </div>

      <main className="main-content">
        {renderContent()}
      </main>
    </div>
  );
}

export default App;