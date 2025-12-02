import React, { useState } from 'react';
import './Admin.css';

function Admin({ onLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!username.trim() || !password.trim()) {
      setError('Please fill in all fields');
      return;
    }

    // Fixed admin credentials: admin / admin123!
    if (username === 'admin' && password === 'admin123!') {
      setIsLoading(true);
      // Simulate API call delay
      setTimeout(() => {
        setIsLoading(false);
        onLogin({ username, password, isAdmin: true });
      }, 500);
    } else {
      setError('Invalid admin credentials');
    }
  };

  return (
    <div className="admin-container">
      <div className="login-box">
        <h2 className="login-title">WhisperLine for Admin</h2>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <input
              type="text"
              className="form-input"
              placeholder="Admin Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <input
              type="password"
              className="form-input"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" className="login-button" disabled={isLoading}>
            {isLoading ? 'Logging in...' : 'Login'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Admin;

