'use client';

/**
 * Authentication Context for Semantic Memory Layer Web Application
 * 
 * Provides JWT-based authentication with secure token storage.
 * NOTE: Tokens are stored in memory only (not localStorage) for security.
 * User data (non-sensitive) is cached in localStorage for faster initial load.
 * 
 * @module lib/auth
 */

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User } from './types';
import { api, getToken, setToken as setApiToken, setUserId as setApiUserId } from './api';

/**
 * Authentication context type definition
 * @interface AuthContextType
 */
interface AuthContextType {
  /** Currently authenticated user or null */
  user: User | null;
  /** JWT token - stored in memory only, null when logged out */
  token: string | null;
  /** Login function to authenticate user */
  login: (token: string, user: User) => void;
  /** Logout function to clear authentication */
  logout: () => void;
  /** Whether auth state is still loading from storage */
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Authentication Provider Component
 * 
 * Wraps application to provide auth state. Token is kept in React state (memory)
 * for security. User data is cached in localStorage to avoid re-fetching on refresh.
 * 
 * @param {ReactNode} children - Child components
 * @returns {JSX.Element} AuthProvider with context
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Restore user from localStorage cache (token cannot be restored for security)
    const savedUser = localStorage.getItem('user');
    
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
        // Note: Token is NOT restored from localStorage for security
        // User will need to re-authenticate if token expires
      } catch {
        localStorage.removeItem('user');
      }
    }
    setIsLoading(false);
  }, []);

/**
   * Login user and store authentication data
   * 
   * Security: Token is stored in memory only (not localStorage).
   * User data is cached in localStorage for faster subsequent loads.
   * 
   * @param {string} tokenValue - JWT token from authentication
   * @param {User} userData - Authenticated user data
   */
  const login = (tokenValue: string, userData: User) => {
    // Store token in memory (via api module) - NOT in localStorage for security
    setApiToken(tokenValue);
    setApiUserId(userData.id);
    setUser(userData);
    
    // Cache user data (non-sensitive) for faster initial load
    // Token is NOT stored - this prevents XSS token theft
    localStorage.setItem('user', JSON.stringify(userData));
  };

  /**
   * Logout current user
   * 
   * Clears all authentication state and calls logout API.
   * Removes user cache from localStorage.
   */
  const logout = async () => {
    try {
      await api.logout();
    } catch {
      // Continue with local cleanup even if API call fails
    }
    // Clear token from memory
    setApiToken(null);
    setApiUserId(null);
    setUser(null);
    // Clear user cache - token already not in localStorage
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ user, token: getToken(), login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook to access authentication context
 * 
 * @throws {Error} If used outside AuthProvider
 * @returns {AuthContextType} Authentication state and functions
 * 
 * @example
 * const { user, logout } = useAuth();
 * if (user) {
 *   console.log(`Logged in as ${user.name}`);
 * }
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}