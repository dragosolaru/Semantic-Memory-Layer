/**
 * Home / Dashboard Page
 * 
 * User dashboard showing quick access to features.
 * Requires authentication to access.
 * 
 * @module app/home/page
 */

'use client';

import Link from 'next/link';
import { useAuth } from '@/lib/auth';
import ProtectedRoute from '../ProtectedRoute';

/**
 * HomePage Component
 * 
 * Dashboard with quick access cards for:
 * - Search
 * - Recent Assets (placeholder)
 * - Upload (placeholder)
 * - Statistics (placeholder)
 * 
 * @returns {JSX.Element} Dashboard page
 */
export default function HomePage() {
  const { user } = useAuth();

  return (
    <ProtectedRoute>
      <div className="dashboard">
        <h1>Welcome, {user?.name}!</h1>
        <div className="dashboard-grid">
          <div className="dashboard-card">
            <h2>Search</h2>
            <p>Search your semantic memory</p>
            <Link href="/search" className="btn-primary">
              Go to Search
            </Link>
          </div>
          <div className="dashboard-card">
            <h2>Recent Assets</h2>
            <p>View your recently accessed items</p>
            <button className="btn-secondary" disabled>View Recent</button>
          </div>
          <div className="dashboard-card">
            <h2>Upload</h2>
            <p>Add new documents to your memory</p>
            <button className="btn-secondary" disabled>Upload Files</button>
          </div>
          <div className="dashboard-card">
            <h2>Statistics</h2>
            <p>View your memory statistics</p>
            <button className="btn-secondary" disabled>View Stats</button>
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
}