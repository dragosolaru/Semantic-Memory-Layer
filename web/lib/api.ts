/**
 * API Client for Semantic Memory Layer
 * 
 * Provides typed API methods with JWT authentication.
 * Token is obtained from memory token store for security.
 * 
 * @module lib/api
 */

import { LoginRequest, RegisterRequest, AuthResponse, SearchRequest, SearchResponse, User, ChangePasswordRequest } from './types';

/** API Base URL from environment or default */
export const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

/**
 * In-memory token store
 * Security: Token is kept in memory, not localStorage
 * This prevents XSS token theft
 */
let currentToken: string | null = null;

/**
 * Get current authentication token
 * @returns {string | null} Current JWT token or null
 */
export function getToken(): string | null {
  return currentToken;
}

/**
 * Set authentication token
 * Called by AuthProvider after login
 * @param {string | null} token - JWT token
 */
export function setToken(token: string | null): void {
  currentToken = token;
}

/**
 * Internal fetch wrapper with authentication
 * @template T - Response type
 * @param {string} endpoint - API endpoint
 * @param {RequestInit} options - Fetch options
 * @returns {Promise<T>} Parsed response
 */
async function fetchApi<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
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

  // Handle empty responses
  const text = await response.text();
  return text ? JSON.parse(text) : (null as T);
}

/**
 * API Methods
 * 
 * @example
 * const response = await api.login({ email: 'user@example.com', password: 'pass' });
 */
export const api = {
  /**
   * Authenticate user with credentials
   * @param {LoginRequest} data - Login credentials
   * @returns {Promise<AuthResponse>} Auth response with token
   */
  login: (data: LoginRequest): Promise<AuthResponse> => 
    fetchApi<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  
  /**
   * Register new user account
   * @param {RegisterRequest} data - Registration data
   * @returns {Promise<AuthResponse>} Auth response with token
   */
  register: (data: RegisterRequest): Promise<AuthResponse> => 
    fetchApi<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  
  /**
   * Get current authenticated user
   * @returns {Promise<User>} Current user data
   */
  getUser: (): Promise<User> => 
    fetchApi<User>('/auth/me', { method: 'GET' }),
  
  /**
   * Search memories
   * @param {SearchRequest} request - Search parameters
   * @returns {Promise<SearchResponse>} Search results
   */
  search: (request: SearchRequest): Promise<SearchResponse> => 
    fetchApi<SearchResponse>('/search', { 
      method: 'POST', 
      body: JSON.stringify(request) 
    }),
  
  /**
   * Change user password
   * @param {ChangePasswordRequest} data - Password change data
   * @returns {Promise<{message: string}>} Success message
   */
  changePassword: (data: ChangePasswordRequest): Promise<{ message: string }> =>
    fetchApi<{ message: string }>('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  /**
   * Logout current user
   * Clears token from memory
   */
  logout: async () => {
    try {
      await fetchApi<void>('/auth/logout', { method: 'POST' });
    } catch {
      // Continue with cleanup even if server call fails
    }
    // Clear token from memory
    setToken(null);
  },
};