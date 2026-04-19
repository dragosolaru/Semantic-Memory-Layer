/**
 * API Client for Semantic Memory Layer
 * 
 * Provides type-safe API communication with the backend.
 * Uses HttpOnly cookies for authentication - no token handling in frontend.
 * All requests include credentials for browser cookie transmission.
 * 
 * Security features:
 * - HttpOnly cookies prevent XSS token theft
 * - credentials: 'include' ensures cookies are sent with requests
 * - Automatic 401 handling redirects to login when session expires
 * - FormData for file uploads (multipart/form-data)
 * 
 * @module lib/api
 * @version 1.0
 * @since 2024
 * 
 * @example
 * import { api } from '@/lib/api';
 * 
 * // Login
 * const response = await api.login({ email: 'user@example.com', password: 'password' });
 * 
 * // Get current user
 * const user = await api.getUser();
 * 
 * // Upload profile image
 * const updatedUser = await api.updateProfileImage(fileInput.files[0]);
 */

import { LoginRequest, RegisterRequest, AuthResponse, SearchRequest, SearchResponse, User, ChangePasswordRequest, UserResponse } from './types';

/**
 * Base URL for API requests.
 * Defaults to local development server if not configured.
 * Set via NEXT_PUBLIC_API_URL environment variable.
 * 
 * @example
 * // .env.local
 * NEXT_PUBLIC_API_URL=http://localhost:8080/api
 */
export const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

/**
 * Request body for profile updates.
 * All fields are optional - only provided fields will be updated.
 */
export interface ProfileUpdateRequest {
  /** User's first name */
  firstName?: string;
  /** User's last name */
  lastName?: string;
  /** User's email address */
  email?: string;
}

/**
 * Request body for profile image URL (legacy - deprecated).
 * @deprecated Use multipart file upload instead.
 */
export interface ProfileImageRequest {
  /** URL of the profile image */
  imageUrl: string;
}

/**
 * Internal fetch wrapper with authentication handling.
 * 
 * Features:
 * - Automatic credentials inclusion for cookie-based auth
 * - JSON content type by default
 * - Configurable redirect on 401 errors
 * - Error handling with user-friendly messages
 * 
 * @template T Expected response type
 * @param endpoint API endpoint path (appended to API_URL)
 * @param options Fetch options (method, body, headers)
 * @param redirectOnAuthError Whether to redirect to login on 401
 * @returns Parsed response data
 * @throws Error on authentication failure or HTTP errors
 * 
 * @security Uses credentials: 'include' to send HttpOnly cookies
 */
async function fetchApi<T>(endpoint: string, options: RequestInit = {}, redirectOnAuthError: boolean = true): Promise<T> {
  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (response.status === 401) {
    if (redirectOnAuthError) {
      window.location.href = '/login';
    }
    throw new Error('Session expired');
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Request failed' }));
    throw new Error(error.message || 'Request failed');
  }

  const text = await response.text();
  return text ? JSON.parse(text) : (null as T);
}

/**
 * API client for Semantic Memory backend.
 * 
 * All methods handle authentication automatically via cookies.
 * File uploads use FormData (multipart/form-data).
 */
export const api = {
  /**
   * Authenticate user with email and password.
   * 
   * @param data Login credentials (email and password)
   * @returns Authentication response with user data and tokens
   * @throws Error on invalid credentials
   */
  login: (data: LoginRequest): Promise<AuthResponse> => 
    fetchApi<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  
  /**
   * Register a new user account.
   * 
   * @param data Registration data (email, password, name)
   * @returns Authentication response with user data and tokens
   * @throws Error on validation failure or duplicate email
   */
  register: (data: RegisterRequest): Promise<AuthResponse> => 
    fetchApi<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  
  /**
   * Get current authenticated user.
   * 
   * Does not redirect on 401 - used for session validation.
   * 
   * @returns User profile data
   * @throws Error if not authenticated
   */
  getUser: (): Promise<UserResponse> => 
    fetchApi<UserResponse>('/auth/me', { method: 'GET' }, false),
  
  /**
   * Update user profile information.
   * 
   * @param data Profile fields to update (all optional)
   * @returns Updated user data
   * @throws Error on validation failure
   */
  updateProfile: (data: ProfileUpdateRequest): Promise<UserResponse> =>
    fetchApi<UserResponse>('/auth/profile', {
      method: 'PUT',
      body: JSON.stringify(data)
    }),
  
  /**
   * Upload and update profile image.
   * 
   * Uses multipart/form-data for secure file upload.
   * Server validates file type (images only) and size (max 5MB).
   * 
   * @param file Image file to upload
   * @returns Updated user data with new image URL
   * @throws Error on invalid file type or size
   * 
   * @security File type validation on server prevents malicious uploads
   */
  updateProfileImage: async (file: File): Promise<UserResponse> => {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch(`${API_URL}/auth/profile/image`, {
      method: 'POST',
      credentials: 'include',
      body: formData
    });
    
    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'Upload failed' }));
      throw new Error(error.message || 'Upload failed');
    }
    
    return response.json();
  },
  
  /**
   * Delete user's profile image.
   * 
   * Removes the profile image and sets it to null.
   * 
   * @returns Updated user data without image
   */
  deleteProfileImage: (): Promise<UserResponse> =>
    fetchApi<UserResponse>('/auth/profile/image', { method: 'DELETE' }),
  
  /**
   * Search semantic memory.
   * 
   * @param request Search query and options
   * @returns Search results with matching memories
   */
  search: (request: SearchRequest): Promise<SearchResponse> => 
    fetchApi<SearchResponse>('/search', { 
      method: 'POST', 
      body: JSON.stringify(request) 
    }),
  
  /**
   * Change user password.
   * 
   * Requires current password for verification.
   * 
   * @param data Current and new password
   * @returns Success message
   * @throws Error on incorrect current password
   */
  changePassword: (data: ChangePasswordRequest): Promise<{ message: string }> =>
    fetchApi<{ message: string }>('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  /**
   * Logout current user.
   * 
   * Clears authentication cookies and invalidates session.
   */
  logout: async () => {
    await fetchApi<void>('/auth/logout', { method: 'POST' });
  },
};