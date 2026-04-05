/**
 * Root Page
 * 
 * Entry point - redirects to dashboard or login.
 * Handles initial auth state check.
 * 
 * @module app/page
 */

'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';

/**
 * RootPage Component
 * 
 * Checks authentication state and redirects:
 * - Authenticated: /home
 * - Not authenticated: /login
 * 
 * @returns {JSX.Element} Loading indicator
 */
export default function RootPage() {
  const { user, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading) {
      if (user) {
        router.push('/home');
      } else {
        router.push('/login');
      }
    }
  }, [user, isLoading, router]);

  return (
    <div className="loading-container">
      <div className="loading">Loading...</div>
    </div>
  );
}