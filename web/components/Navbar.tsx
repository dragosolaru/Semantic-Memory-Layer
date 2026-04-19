'use client';

import Link from 'next/link';
import { useAuth } from '@/lib/auth';
import { useRouter } from 'next/navigation';
import { API_URL } from '@/lib/api';

export default function Navbar() {
  const { user, logout, isLoading } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  const getUserDisplay = () => {
    const name = user?.firstName || user?.name || 'User';
    const initial = name.charAt(0).toUpperCase();
    return { name, initial };
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
            <Link href="/profile" className="user-avatar-container">
              {user.profileImageUrl ? (
                <img 
                  src={user.profileImageUrl.startsWith('http') ? user.profileImageUrl : `${API_URL}/${user.profileImageUrl}`}
                  alt="Profile" 
                  className="user-avatar-img"
                />
              ) : (
                <div className="user-avatar-placeholder">
                  {getUserDisplay().initial}
                </div>
              )}
              <span className="user-tooltip">{getUserDisplay().name}</span>
            </Link>
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