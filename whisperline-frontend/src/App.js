import React, { useState, useEffect } from 'react';
import './App.css';
import MessageBox from './components/MessageBox';
import InputBox from './components/InputBox';
import Login from './components/Login';
import Signup from './components/Signup';
import Admin from './components/Admin';
import Dashboard from './components/Dashboard';
import { sendMessage } from './services/api';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentView, setCurrentView] = useState('login');
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const path = window.location.pathname;
    if (path === '/admin') {
      setCurrentView('admin');
    }
  }, []);
  const [messages, setMessages] = useState([
    { id: 1, message: 'Hello! How can I help you?', sender: 'assistant', timestamp: '10:30' }
  ]);
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = (credentials) => {
    setIsAuthenticated(true);
    if (credentials.isAdmin) {
      setIsAdmin(true);
    }
  };

  const handleAdminLogin = () => {
    setIsAuthenticated(true);
    setIsAdmin(true);
  };

  const handleSignup = () => {
    setIsAuthenticated(true);
  };

  const handleNavigateToSignup = () => {
    setCurrentView('signup');
  };

  const handleNavigateToLogin = () => {
    setCurrentView('login');
  };

  const handleNavigateToAdmin = () => {
    setCurrentView('admin');
  };

  const handleSend = async (message) => {
    const userMessage = {
      id: messages.length + 1,
      message,
      sender: 'user',
      timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
    };
    
    setMessages([...messages, userMessage]);
    setIsLoading(true);

    try {
      const response = await sendMessage(message);
      
      if (response.error) {
        const errorMessage = {
          id: messages.length + 2,
          message: response.error,
          sender: 'assistant',
          timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
        };
        setMessages(prev => [...prev, errorMessage]);
      } else {
        const assistantMessage = {
          id: messages.length + 2,
          message: response.response || 'No response received',
          sender: 'assistant',
          timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
        };
        setMessages(prev => [...prev, assistantMessage]);
      }
    } catch (error) {
      const errorMessage = {
        id: messages.length + 2,
        message: error.message || 'Failed to send message. Please try again.',
        sender: 'assistant',
        timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isAuthenticated) {
    if (currentView === 'admin') {
      return <Admin onLogin={handleAdminLogin} />;
    }
    if (currentView === 'signup') {
      return <Signup onSignup={handleSignup} onNavigateToLogin={handleNavigateToLogin} />;
    }
    return <Login onLogin={handleLogin} onNavigateToSignup={handleNavigateToSignup} onNavigateToAdmin={handleNavigateToAdmin} />;
  }

  if (isAdmin) {
    return <Dashboard />;
  }

  return (
    <div className="App">
      <header className="app-header">
        WhisperLine
      </header>
      <div className="chat-container">
        {messages.map((msg) => (
          <MessageBox
            key={msg.id}
            message={msg.message}
            sender={msg.sender}
            timestamp={msg.timestamp}
          />
        ))}
      </div>
      <InputBox onSend={handleSend} isLoading={isLoading} />
    </div>
  );
}

export default App;
