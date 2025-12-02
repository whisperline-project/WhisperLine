import React, { useState } from 'react';
import './Signup.css';
import { signup } from '../services/api';

function Signup({ onSignup, onNavigateToLogin }) {
  const [formData, setFormData] = useState({
    name: '',
    username: '',
    password: '',
    email: ''
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!formData.name.trim() || !formData.username.trim() || 
        !formData.password.trim() || !formData.email.trim()) {
      setError('Please fill in all fields');
      return;
    }

    setIsLoading(true);
    const response = await signup(formData);
    setIsLoading(false);

    if (response.success) {
      onSignup(formData);
    } else {
      setError(response.message || 'Signup failed');
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-box">
        <h2 className="signup-title">Create Account</h2>
        <form onSubmit={handleSubmit} className="signup-form">
          <div className="form-group">
            <input
              type="text"
              name="name"
              className="form-input"
              placeholder="Name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <input
              type="text"
              name="username"
              className="form-input"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <input
              type="password"
              name="password"
              className="form-input"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <input
              type="email"
              name="email"
              className="form-input"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" className="signup-button" disabled={isLoading}>
            {isLoading ? 'Signing up...' : 'Sign Up'}
          </button>
        </form>
        <div className="login-link-container">
          <span className="login-text">Already have an account? </span>
          <button className="login-link" onClick={onNavigateToLogin}>
            Login
          </button>
        </div>
      </div>
    </div>
  );
}

export default Signup;

