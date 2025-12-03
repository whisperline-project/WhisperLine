import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import { getTopRiskUsers, getRiskAlerts } from '../services/api';

function Dashboard() {
  const [topUsers, setTopUsers] = useState([]);
  const [riskAlerts, setRiskAlerts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingAlerts, setIsLoadingAlerts] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchTopRiskUsers();
    fetchRiskAlerts();
  }, []);

  const fetchTopRiskUsers = async () => {
    try {
      setIsLoading(true);
      const data = await getTopRiskUsers();
      setTopUsers(data);
      setError('');
    } catch (err) {
      setError('Failed to load dashboard data');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchRiskAlerts = async () => {
    try {
      setIsLoadingAlerts(true);
      const data = await getRiskAlerts();
      setRiskAlerts(data);
    } catch (err) {
      // Error handled silently - alerts section will show empty state
    } finally {
      setIsLoadingAlerts(false);
    }
  };

  const refreshAll = () => {
    fetchTopRiskUsers();
    fetchRiskAlerts();
  };

  const formatTimestamp = (timestamp) => {
    if (!timestamp) return '';
    
    const date = new Date(timestamp);
    
    // Check if date is valid
    if (isNaN(date.getTime())) {
      return 'Invalid date';
    }
    
    const now = new Date();
    const diffMs = Math.abs(now - date);
    
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    return date.toLocaleDateString();
  };

  const getRiskLevelColor = (riskLevel) => {
    if (riskLevel >= 70) return '#dc3545';
    if (riskLevel >= 50) return '#fd7e14';
    if (riskLevel >= 30) return '#ffc107';
    return '#28a745';
  };

  const getRiskLevelLabel = (riskLevel) => {
    if (riskLevel >= 70) return 'High';
    if (riskLevel >= 50) return 'Medium-High';
    if (riskLevel >= 30) return 'Medium';
    return 'Low';
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <button onClick={refreshAll} className="refresh-button">
          Refresh
        </button>
      </div>

      {isLoading && isLoadingAlerts ? (
        <div className="loading">Loading dashboard data...</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : (
        <>
          {/* Risk Alerts Section */}
          <div className="dashboard-content">
            <h2>Real-time Risk Alerts</h2>
            {isLoadingAlerts ? (
              <div className="loading">Loading risk alerts...</div>
            ) : (
              <div className="table-container">
                <table className="risk-table">
                  <thead>
                    <tr>
                      <th>Time</th>
                      <th>Username</th>
                      <th>Name</th>
                      <th>Message</th>
                      <th>Risk Level</th>
                      <th>Risk Category</th>
                    </tr>
                  </thead>
                  <tbody>
                    {riskAlerts.length === 0 ? (
                      <tr>
                        <td colSpan="6" className="no-data">No risk alerts available</td>
                      </tr>
                    ) : (
                      riskAlerts.map((alert, index) => (
                        <tr key={`${alert.username}-${alert.timestamp}-${index}`}>
                          <td>{formatTimestamp(alert.timestamp)}</td>
                          <td>{alert.username}</td>
                          <td>{alert.name}</td>
                          <td className="message-cell">{alert.message.length > 100 ? alert.message.substring(0, 100) + '...' : alert.message}</td>
                          <td>
                            <span 
                              className="risk-level-badge"
                              style={{ backgroundColor: getRiskLevelColor(alert.riskLevel) }}
                            >
                              {alert.riskLevel}
                            </span>
                          </td>
                          <td>{getRiskLevelLabel(alert.riskLevel)}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Top 10 Users Section */}
          <div className="dashboard-content">
            <h2>Top 10 Users by Average Risk Level</h2>
          <div className="table-container">
            <table className="risk-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Username</th>
                  <th>Name</th>
                  <th>Average Risk Level</th>
                  <th>Risk Category</th>
                  <th>Message Count</th>
                </tr>
              </thead>
              <tbody>
                {topUsers.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="no-data">No data available</td>
                  </tr>
                ) : (
                  topUsers.map((user, index) => (
                    <tr key={user.username}>
                      <td>{index + 1}</td>
                      <td>{user.username}</td>
                      <td>{user.name}</td>
                      <td>
                        <span 
                          className="risk-level-badge"
                          style={{ backgroundColor: getRiskLevelColor(user.averageRiskLevel) }}
                        >
                          {user.averageRiskLevel.toFixed(2)}
                        </span>
                      </td>
                      <td>{getRiskLevelLabel(user.averageRiskLevel)}</td>
                      <td>{user.messageCount}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          </div>
        </>
      )}
    </div>
  );
}

export default Dashboard;
