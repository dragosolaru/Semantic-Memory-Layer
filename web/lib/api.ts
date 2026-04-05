import { LoginRequest, RegisterRequest, AuthResponse, User } from './types';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

async function fetchApi<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Request failed' }));
    throw new Error(error.message || 'Request failed');
  }

  return response.json();
}

export const api = {
  login: (data: LoginRequest) => 
    fetchApi<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  
  register: (data: RegisterRequest) => 
    fetchApi<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  
  getUser: () => 
    fetchApi<User>('/auth/me', { method: 'GET' }),
  
  search: (query: string) => 
    fetchApi<{ results: unknown[] }>('/search', { 
      method: 'POST', 
      body: JSON.stringify({ query }) 
    }),
  
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};