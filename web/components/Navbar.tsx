'use client';

import Link from 'next/link';
import { useAuth } from '@/lib/auth';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function Navbar() {
  const { user, logout, isLoading } = useAuth();
  const router = useRouter();

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