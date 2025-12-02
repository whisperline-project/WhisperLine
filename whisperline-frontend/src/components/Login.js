import React, { useState } from 'react';
import './Login.css';

function Login({ onLogin, onNavigateToSignup }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (username.trim() && password.trim()) {
      onLogin({ username, password });
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
          <button type="submit" className="login-button">
            Login
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

