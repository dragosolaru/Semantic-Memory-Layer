'use client';

import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { api, API_URL, ProfileUpdateRequest } from '@/lib/api';

export default function ProfilePage() {
  const { user, login, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [imageUploading, setImageUploading] = useState(false);

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/login');
    }
  }, [authLoading, user, router]);

  useEffect(() => {
    if (user) {
      setFirstName(user.firstName || '');
      setLastName(user.lastName || '');
      setEmail(user.email || '');
    }
  }, [user]);

  const handleProfileUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const request: ProfileUpdateRequest = {};
      if (firstName !== (user?.firstName || '')) request.firstName = firstName;
      if (lastName !== (user?.lastName || '')) request.lastName = lastName;
      if (email !== user?.email) request.email = email;

      if (Object.keys(request).length === 0) {
        setMessage('No changes to save');
        return;
      }

      const updatedUser = await api.updateProfile(request);
      login({ ...user!, ...updatedUser });
      setMessage('Profile updated successfully');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Update failed');
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');

    if (newPassword !== confirmPassword) {
      setError('New passwords do not match');
      setLoading(false);
      return;
    }

    if (newPassword.length < 6) {
      setError('Password must be at least 6 characters');
      setLoading(false);
      return;
    }

    try {
      await api.changePassword({
        email,
        currentPassword,
        newPassword
      });
      setMessage('Password changed successfully');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Password change failed');
    } finally {
      setLoading(false);
    }
  };

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      setError('Image must be less than 5MB');
      return;
    }

    setImageUploading(true);
    setError('');
    setMessage('');

    try {
      const updatedUser = await api.updateProfileImage(file);
      login({ ...user!, ...updatedUser });
      setMessage('Profile image updated successfully');
      setImageUploading(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Image upload failed');
      setImageUploading(false);
    }
  };

  const handleDeleteImage = async () => {
    if (!confirm('Are you sure you want to remove your profile photo?')) return;

    setImageUploading(true);
    setError('');
    setMessage('');

    try {
      const updatedUser = await api.deleteProfileImage();
      login({ ...user!, ...updatedUser });
      setMessage('Profile image removed successfully');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to remove image');
    } finally {
      setImageUploading(false);
    }
  };

  if (authLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (!user) {
    return null;
  }

  return (
    <div className="profile-container">
      <h1>Profile</h1>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <div className="profile-section">
        <h2>Profile Picture</h2>
        <div className="profile-image-container">
          <div className="profile-image">
            {user.profileImageUrl ? (
              <img src={user.profileImageUrl.startsWith('http') ? user.profileImageUrl : `${API_URL}/${user.profileImageUrl}`} alt="Profile" />
            ) : (
              <div className="profile-image-placeholder">
                {user.firstName?.charAt(0) || user.name?.charAt(0) || '?'}
              </div>
            )}
          </div>
          <input
            type="file"
            ref={fileInputRef}
            accept="image/*"
            onChange={handleImageUpload}
            style={{ display: 'none' }}
          />
          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            disabled={imageUploading}
            className="btn-secondary"
          >
            {imageUploading ? 'Uploading...' : 'Change Photo'}
          </button>
          {user.profileImageUrl && (
            <button
              type="button"
              onClick={handleDeleteImage}
              disabled={imageUploading}
              className="btn-danger"
              style={{ marginLeft: '8px' }}
            >
              {imageUploading ? 'Deleting...' : 'Remove Photo'}
            </button>
          )}
        </div>
      </div>

      <div className="profile-section">
        <h2>Personal Information</h2>
        <form onSubmit={handleProfileUpdate}>
          <div className="form-group">
            <label htmlFor="firstName">First Name</label>
            <input
              type="text"
              id="firstName"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="lastName">Last Name</label>
            <input
              type="text"
              id="lastName"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Saving...' : 'Save Changes'}
          </button>
        </form>
      </div>

      <div className="profile-section">
        <h2>Change Password</h2>
        <form onSubmit={handlePasswordChange}>
          <div className="form-group">
            <label htmlFor="currentPassword">Current Password</label>
            <input
              type="password"
              id="currentPassword"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="newPassword">New Password</label>
            <input
              type="password"
              id="newPassword"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm New Password</label>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Changing...' : 'Change Password'}
          </button>
        </form>
      </div>

      <div className="profile-section">
        <h2>Account Details</h2>
        <div className="account-info">
          <p><strong>User ID:</strong> {user.id}</p>
          <p><strong>Subscription:</strong> {user.subscriptionTier}</p>
        </div>
      </div>
    </div>
  );
}