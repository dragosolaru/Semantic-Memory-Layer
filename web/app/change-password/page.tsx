/**
 * Change Password Page
 * 
 * User password change settings.
 * Requires authentication to access.
 * 
 * @module app/change-password/page
 */

'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { api } from '@/lib/api';

/** Local form state interface */
interface ChangePasswordForm {
  /** Current password for verification */
  currentPassword: string;
  /** New password to set */
  newPassword: string;
  /** Password confirmation */
  confirmPassword: string;
}

/**
 * ChangePasswordPage Component
 * 
 * Form for changing user password.
 * Requires current password for verification.
 * Minimum new password length: 6 characters.
 * 
 * @returns {JSX.Element} Change password form
 */
export default function ChangePasswordPage() {
  const [formData, setFormData] = useState<ChangePasswordForm>({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useAuth();
  const router = useRouter();

  /**
   * Handle form submission
   * Validates passwords and calls change password API
   * @param {FormEvent} e - Form submit event
   */
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validate password match
    if (formData.newPassword !== formData.confirmPassword) {
      setError('New passwords do not match');
      return;
    }

    // Validate password length
    if (formData.newPassword.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setIsLoading(true);

    try {
      await api.changePassword({
        email: user?.email || '',
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword,
      });

      setSuccess('Password changed successfully');
      setTimeout(() => router.push('/home'), 2000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to change password');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Change Password</h1>
        <form onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}
          <div className="form-group">
            <label htmlFor="currentPassword">Current Password</label>
            <input
              id="currentPassword"
              type="password"
              value={formData.currentPassword}
              onChange={(e) => setFormData({ ...formData, currentPassword: e.target.value })}
              required
              placeholder="Enter current password"
            />
          </div>
          <div className="form-group">
            <label htmlFor="newPassword">New Password</label>
            <input
              id="newPassword"
              type="password"
              value={formData.newPassword}
              onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
              required
              placeholder="Enter new password"
              minLength={6}
            />
          </div>
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm New Password</label>
            <input
              id="confirmPassword"
              type="password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              required
              placeholder="Confirm new password"
              minLength={6}
            />
          </div>
          <button type="submit" disabled={isLoading} className="btn-primary">
            {isLoading ? 'Changing...' : 'Change Password'}
          </button>
        </form>
      </div>
    </div>
  );
}