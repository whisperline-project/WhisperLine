import React, { useState } from 'react';
import './Login.css';
import { login } from '../services/api';

function Login({ onLogin, onNavigateToSignup }) {
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

    setIsLoading(true);
    const response = await login({ username, password });
    setIsLoading(false);

    if (response.success) {
      onLogin({ username, password });
    } else {
      setError(response.message || 'Login failed');
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2 className="login-title">WhisperLine</h2>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <input
              type="text"
              className="form-input"
              placeholder="Username"
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
        <div className="signup-link-container">
          <span className="signup-text">Don't have an account? </span>
          <button className="signup-link" onClick={onNavigateToSignup}>
            Sign up
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;

