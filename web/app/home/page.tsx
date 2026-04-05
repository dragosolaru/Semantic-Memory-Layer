'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/lib/auth';

export default function HomePage() {
  const { user, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !user) {
      router.push('/login');
    }
  }, [user, isLoading, router]);

  if (isLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (!user) {
    return null;
  }

  return (
    <div className="dashboard">
      <h1>Welcome, {user.name}!</h1>
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
          <button className="btn-secondary">View Recent</button>
        </div>
        <div className="dashboard-card">
          <h2>Upload</h2>
          <p>Add new documents to your memory</p>
          <button className="btn-secondary">Upload Files</button>
        </div>
        <div className="dashboard-card">
          <h2>Statistics</h2>
          <p>View your memory statistics</p>
          <button className="btn-secondary">View Stats</button>
        </div>
      </div>
    </div>
  );
}