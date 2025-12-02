import React, { useState } from 'react';
import './App.css';
import MessageBox from './components/MessageBox';
import InputBox from './components/InputBox';
import { sendMessage } from './services/api';

function App() {
  const [messages, setMessages] = useState([
    { id: 1, message: 'Hello! How can I help you?', sender: 'assistant', timestamp: '10:30' }
  ]);
  const [isLoading, setIsLoading] = useState(false);

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
      console.log('API Response:', response);
      
      if (response.error) {
        const errorMessage = {
          id: messages.length + 2,
          message: response.error,
          sender: 'assistant',
          timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
        };
        setMessages(prev => [...prev, errorMessage]);
      } else {
        console.log('Response data:', response);
        console.log('Response.response:', response.response);
        const assistantMessage = {
          id: messages.length + 2,
          message: response.response || 'No response received',
          sender: 'assistant',
          timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
        };
        console.log('Assistant message to add:', assistantMessage);
        setMessages(prev => [...prev, assistantMessage]);
      }
    } catch (error) {
      console.error('Failed to send message:', error);
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
