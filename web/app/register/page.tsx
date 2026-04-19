/**
 * Registration Page
 * 
 * New user registration form with name, email, and password.
 * On success, automatically logs in and redirects to dashboard.
 * 
 * @module app/register/page
 */

'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/lib/auth';
import { api } from '@/lib/api';

/**
 * RegisterPage Component
 * 
 * Renders registration form with name, email, password inputs.
 * Minimum password length: 6 characters.
 * On success, authenticates and redirects to dashboard.
 * 
 * @returns {JSX.Element} Registration form
 */
export default function RegisterPage() {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const router = useRouter();

  /**
   * Handle form submission
   * Creates account, authenticates, and redirects on success
   * @param {FormEvent} e - Form submit event
   */
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await api.register({ firstName, lastName, email, password });
      login(response.user);
      router.push('/home');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Create Account</h1>
        <form onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}
          <div className="form-group">
            <label htmlFor="firstName">First Name</label>
            <input
              id="firstName"
              type="text"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              required
              placeholder="Enter your first name"
            />
          </div>
          <div className="form-group">
            <label htmlFor="lastName">Last Name</label>
            <input
              id="lastName"
              type="text"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              placeholder="Enter your last name"
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="Enter your email"
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="Create a password"
              minLength={6}
            />
          </div>
          <button type="submit" disabled={isLoading} className="btn-primary">
            {isLoading ? 'Creating account...' : 'Register'}
          </button>
        </form>
        <p className="auth-link">
          Already have an account? <Link href="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}