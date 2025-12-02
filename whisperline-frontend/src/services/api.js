const API_BASE_URL = 'http://localhost:8080/api';
const CHAT_API_URL = `${API_BASE_URL}/chat`;
const AUTH_API_URL = `${API_BASE_URL}/auth`;

export const sendMessage = async (message, userId = 'user1') => {
  try {
    const response = await fetch(`${CHAT_API_URL}/message`, {
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

export const signup = async (userData) => {
  try {
    const response = await fetch(`${AUTH_API_URL}/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: userData.name,
        username: userData.username,
        password: userData.password,
        email: userData.email
      })
    });

    const data = await response.json();
    
    if (!response.ok) {
      return {
        success: false,
        message: data.message || 'Signup failed'
      };
    }

    return data;
  } catch (error) {
    console.error('Error signing up:', error);
    return {
      success: false,
      message: 'Failed to connect to server'
    };
  }
};

export const login = async (credentials) => {
  try {
    const response = await fetch(`${AUTH_API_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: credentials.username,
        password: credentials.password
      })
    });

    const data = await response.json();
    
    if (!response.ok) {
      return {
        success: false,
        message: data.message || 'Login failed'
      };
    }

    return data;
  } catch (error) {
    console.error('Error logging in:', error);
    return {
      success: false,
      message: 'Failed to connect to server'
    };
  }
};

