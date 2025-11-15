import React, { useState } from 'react';
import './App.css';
import MessageBox from './components/MessageBox';
import InputBox from './components/InputBox';

function App() {
  const [messages, setMessages] = useState([
    { id: 1, message: 'Hello! How can I help you?', sender: 'assistant', timestamp: '10:30' },
    { id: 2, message: 'Hello', sender: 'user', timestamp: '10:31' }
  ]);

  const handleSend = (message) => {
    const newMessage = {
      id: messages.length + 1,
      message,
      sender: 'user',
      timestamp: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
    };
    setMessages([...messages, newMessage]);
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
      <InputBox onSend={handleSend} />
    </div>
  );
}

export default App;
