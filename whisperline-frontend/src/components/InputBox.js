import React, { useState } from 'react';
import './InputBox.css';

function InputBox({ onSend, placeholder = "Type a message..." }) {
  const [input, setInput] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (input.trim()) {
      onSend(input);
      setInput('');
    }
  };

  return (
    <form className="input-box" onSubmit={handleSubmit}>
      <input
        type="text"
        className="input-field"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder={placeholder}
      />
      <button type="submit" className="send-button">
        Send
      </button>
    </form>
  );
}

export default InputBox;

