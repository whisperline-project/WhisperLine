import React from 'react';
import './MessageBox.css';

function MessageBox({ message, sender, timestamp }) {
  return (
    <div className={`message-box ${sender}`}>
      <div className="message-content">
        {message}
      </div>
      {timestamp && <div className="message-timestamp">{timestamp}</div>}
    </div>
  );
}

export default MessageBox;

