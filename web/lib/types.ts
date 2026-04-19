/**
 * TypeScript Type Definitions for Semantic Memory Layer
 * 
 * @module lib/types
 */

/**
 * User entity represents an authenticated user
 * @interface User
 */
export interface User {
  /** Unique identifier */
  id: string;
  /** User email address */
  email: string;
  /** First name */
  firstName?: string;
  /** Last name */
  lastName?: string;
  /** Display name (computed) */
  name?: string;
  /** Profile image URL */
  profileImageUrl?: string;
  /** Subscription tier (optional) */
  subscriptionTier?: string;
}

/**
 * Authentication response from login/register
 * @interface AuthResponse
 */
export interface AuthResponse {
  token?: string;
  refreshToken?: string;
  type?: string;
  expiresIn?: number;
  user: UserResponse;
}

export interface UserResponse {
  id: string;
  email: string;
  firstName?: string;
  lastName?: string;
  name?: string;
  profileImageUrl?: string;
  subscriptionTier?: string;
}

/**
 * Login credentials
 * @interface LoginRequest
 */
export interface LoginRequest {
  /** User email */
  email: string;
  /** User password */
  password: string;
}

/**
 * Registration data
 * @interface RegisterRequest
 */
export interface RegisterRequest {
  /** User email */
  email: string;
  /** User password */
  password: string;
  /** User first name */
  firstName?: string;
  /** User last name */
  lastName?: string;
}

/**
 * Password change request
 * @interface ChangePasswordRequest
 */
export interface ChangePasswordRequest {
  /** User email */
  email: string;
  /** Current password */
  currentPassword: string;
  /** New password */
  newPassword: string;
}

/**
 * Search query parameters
 * @interface SearchRequest
 */
export interface SearchRequest {
  /** Search query text */
  query: string;
  /** Filter by asset types */
  assetType?: string[];
  /** Filter by source ID */
  sourceId?: string;
  /** Filter by start date (ISO string) */
  dateFrom?: string;
  /** Filter by end date (ISO string) */
  dateTo?: string;
  /** Filter by location */
  location?: string;
  /** Page number (0-indexed) */
  page?: number;
  /** Results per page */
  pageSize?: number;
}

/**
 * Search response with results
 * @interface SearchResponse
 */
export interface SearchResponse {
  /** Search results */
  results: SearchResult[];
  /** Total matching results */
  total: number;
  /** Current page number */
  page: number;
  /** Results per page */
  pageSize: number;
  /** Whether more results exist */
  hasMore: boolean;
  /** Query analysis from server */
  queryAnalysis?: QueryAnalysis;
}

/**
 * Individual search result
 * @interface SearchResult
 */
export interface SearchResult {
  /** Associated asset */
  asset: Asset;
  /** Relevance score (0-1) */
  score: number;
  /** Matched entity names */
  matchedEntities: string[];
  /** Highlighted matching text */
  highlightedText: string;
}

/**
 * File or document asset
 * @interface Asset
 */
export interface Asset {
  /** Unique identifier */
  id: string;
  /** File name */
  fileName: string;
  /** MIME type */
  fileType: string;
  /** Storage path */
  filePath: string;
  /** File size in bytes */
  fileSizeBytes: number;
  /** Last modification date (ISO string) */
  modifiedAt: string;
}

/**
 * Server-side query analysis
 * @interface QueryAnalysis
 */
export interface QueryAnalysis {
  /** Identified intent */
  intent: string;
  /** Extracted entities */
  entities: ParsedEntity[];
  /** Applied filters */
  filters?: SearchRequest;
}

/**
 * Parsed entity from query
 * @interface ParsedEntity
 */
export interface ParsedEntity {
  /** Entity type */
  type: string;
  /** Entity value */
  value: string;
}