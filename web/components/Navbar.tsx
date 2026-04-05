/**
 * Navbar Component
 * 
 * Application navigation bar with branding and auth controls.
 * Shows different links based on authentication state.
 * 
 * @module components/Navbar
 */

'use client';

import Link from 'next/link';
import { useAuth } from '@/lib/auth';
import { useRouter } from 'next/navigation';

/**
 * Navbar Component
 * 
 * Responsive navigation with:
 * - Brand link to home
 * - Authenticated: Home, Search, Settings, User name, Logout
 * - Unauthenticated: Login, Register
 * 
 * @returns {JSX.Element} Navigation bar
 */
export default function Navbar() {
  const { user, logout, isLoading } = useAuth();
  const router = useRouter();

  /**
   * Handle user logout
   * Clears auth state and redirects to login
   */
  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  if (isLoading) return null;

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <Link href="/home">Semantic Memory</Link>
      </div>
      <div className="nav-links">
        {user ? (
          <>
            <Link href="/home">Home</Link>
            <Link href="/search">Search</Link>
            <Link href="/change-password">Settings</Link>
            <span className="user-name">{user.name}</span>
            <button onClick={handleLogout} className="btn-logout">
              Logout
            </button>
          </>
        ) : (
          <>
            <Link href="/login">Login</Link>
            <Link href="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}