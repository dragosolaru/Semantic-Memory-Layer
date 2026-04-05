import { LoginRequest, RegisterRequest, AuthResponse, SearchRequest, SearchResponse, User } from './types';

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
  login: (data: LoginRequest): Promise<AuthResponse> => 
    fetchApi<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  
  register: (data: RegisterRequest): Promise<AuthResponse> => 
    fetchApi<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  
  getUser: (): Promise<User> => 
    fetchApi<User>('/auth/me', { method: 'GET' }),
  
  search: (request: SearchRequest): Promise<SearchResponse> => 
    fetchApi<SearchResponse>('/search', { 
      method: 'POST', 
      body: JSON.stringify(request) 
    }),
  
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};