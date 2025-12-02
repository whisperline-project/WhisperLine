const API_BASE_URL = 'http://localhost:8080/api/chat';

export const sendMessage = async (message, userId = 'user1') => {
  try {
    const response = await fetch(`${API_BASE_URL}/message`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        message: message,
        userId: userId
      })
    });

    const data = await response.json();
    
    if (!response.ok) {
      return {
        error: data.error || `HTTP error! status: ${response.status}`,
        response: null
      };
    }

    return data;
  } catch (error) {
    console.error('Error sending message:', error);
    throw error;
  }
};

