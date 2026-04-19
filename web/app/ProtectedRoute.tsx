/**
 * Protected Route Component
 * 
 * Wraps content that requires authentication.
 * Automatically redirects to login if user is not authenticated.
 * 
 * @module app/ProtectedRoute
 */

'use client';

import { useAuth } from '@/lib/auth';
import { useRouter, usePathname } from 'next/navigation';
import { useEffect, ReactNode } from 'react';

/**
 * Props for ProtectedRoute component
 * @interface ProtectedRouteProps
 */
interface ProtectedRouteProps {
  /** Content to render if authenticated */
  children: ReactNode;
}

/**
 * ProtectedRoute Component
 * 
 * Guards wrapped content, redirecting unauthenticated users to login.
 * Preserves current path for redirect after successful login.
 * 
 * @param {ProtectedRouteProps} props - Component props
 * @returns {JSX.Element} Protected content or redirect
 * 
 * @example
 * <ProtectedRoute>
 *   <Dashboard />
 * </ProtectedRoute>
 */
export default function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { user, isLoading } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (!isLoading && !user) {
      // Only redirect to login if there's no valid session
      // Use replace to avoid history stack buildup
      router.replace(`/login?redirect=${encodeURIComponent(pathname)}`);
    }
  }, [user, isLoading, router, pathname]);

  // Show loading while checking auth
  if (isLoading) {
    return <div className="loading">Loading...</div>;
  }

  // Don't render anything while redirecting
  if (!user) {
    return null;
  }

  return <>{children}</>;
}