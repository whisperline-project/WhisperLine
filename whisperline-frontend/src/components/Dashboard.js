import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import { getTopRiskUsers } from '../services/api';

function Dashboard() {
  const [topUsers, setTopUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchTopRiskUsers();
  }, []);

  const fetchTopRiskUsers = async () => {
    try {
      setIsLoading(true);
      const data = await getTopRiskUsers();
      setTopUsers(data);
      setError('');
    } catch (err) {
      setError('Failed to load dashboard data');
      console.error('Error:', err);
    } finally {
      setIsLoading(false);
    }
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
        <button onClick={fetchTopRiskUsers} className="refresh-button">
          Refresh
        </button>
      </div>

      {isLoading ? (
        <div className="loading">Loading dashboard data...</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : (
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
      )}
    </div>
  );
}

export default Dashboard;

