import React, { useState } from 'react';
import './Signup.css';

function Signup({ onSignup, onNavigateToLogin }) {
  const [formData, setFormData] = useState({
    name: '',
    username: '',
    password: '',
    email: ''
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (formData.name.trim() && formData.username.trim() && 
        formData.password.trim() && formData.email.trim()) {
      onSignup(formData);
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
          <button type="submit" className="signup-button">
            Sign Up
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

