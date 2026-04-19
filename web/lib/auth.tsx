'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User } from './types';
import { api } from './api';

/**
 * Authentication context type definition.
 * 
 * Provides authentication state and methods to child components.
 */
interface AuthContextType {
  /** Current authenticated user or null if not logged in */
  user: User | null;
  /**
   * Login function to set user in context.
   * Also persists user to localStorage for session persistence.
   */
  login: (user: User) => void;
  /**
   * Logout function that clears session.
   * Calls backend logout endpoint and clears local storage.
   */
  logout: () => void;
  /** Loading state while checking authentication */
  isLoading: boolean;
}

/**
 * React Context for authentication state.
 * Use useAuth() hook to access authentication methods.
 */
const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Authentication Provider Component
 * 
 * Wraps the application to provide authentication state and methods.
 * Handles:
 * - Initial session validation on app load
 * - User data persistence in localStorage
 * - Backend session verification
 * 
 * Security features:
 * - Validates session with backend on mount
 * - Clears invalid sessions automatically
 * - Uses HttpOnly cookies for authentication
 * 
 * @param {ReactNode} children - Child components that need auth access
 * @returns AuthContext.Provider with authentication state
 * 
 * @example
 * // In layout.tsx
 * import { AuthProvider } from '@/lib/auth';
 * 
 * export default function RootLayout({ children }) {
 *   return (
 *     <AuthProvider>
 *       {children}
 *     </AuthProvider>
 *   );
 * }
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  /**
   * Initialize authentication on component mount.
   * 
   * Process:
   * 1. Check localStorage for cached user data
   * 2. Validate session with backend API
   * 3. Update state based on validation result
   * 
   * This ensures sessions persist across page refreshes.
   */
  useEffect(() => {
    const initAuth = async () => {
      // First, try to get cached user from localStorage
      const savedUser = localStorage.getItem('user');
      
      if (savedUser) {
        try {
          setUser(JSON.parse(savedUser));
        } catch {
          localStorage.removeItem('user');
        }
      }
      
      // Then validate session with backend
      try {
        const backendUser = await api.getUser();
        setUser(backendUser);
        localStorage.setItem('user', JSON.stringify(backendUser));
      } catch {
        // No valid session - clear any cached data
        setUser(null);
        localStorage.removeItem('user');
      }
      
      setIsLoading(false);
    };

    initAuth();
  }, []);

  /**
   * Login function - sets user in context and localStorage.
   * 
   * @param userData - User data from successful authentication
   */
  const login = (userData: User) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  /**
   * Logout function - clears session on backend and locally.
   * 
   * Calls logout endpoint to invalidate server session,
   * then clears local state and storage.
   */
  const logout = async () => {
    try {
      await api.logout();
    } catch {
      // Continue with cleanup even if backend call fails
    }
    setUser(null);
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook to access authentication context.
 * 
 * Must be used within an AuthProvider.
 * 
 * @returns Authentication context with user, login, logout, and isLoading
 * @throws Error if used outside AuthProvider
 * 
 * @example
 * import { useAuth } from '@/lib/auth';
 * 
 * function MyComponent() {
 *   const { user, logout } = useAuth();
 *   
 *   return (
 *     <div>
 *       <p>Welcome, {user?.name}</p>
 *       <button onClick={logout}>Logout</button>
 *     </div>
 *   );
 * }
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}