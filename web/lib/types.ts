export interface User {
  id: string;
  email: string;
  name: string;
  subscriptionTier?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface SearchRequest {
  query: string;
  assetType?: string[];
  sourceId?: string;
  dateFrom?: string;
  dateTo?: string;
  location?: string;
  page?: number;
  pageSize?: number;
}

export interface SearchResponse {
  results: SearchResult[];
  total: number;
  page: number;
  pageSize: number;
  hasMore: boolean;
  queryAnalysis?: QueryAnalysis;
}

export interface SearchResult {
  asset: Asset;
  score: number;
  matchedEntities: string[];
  highlightedText: string;
}

export interface Asset {
  id: string;
  fileName: string;
  fileType: string;
  filePath: string;
  fileSizeBytes: number;
  modifiedAt: string;
}

export interface QueryAnalysis {
  intent: string;
  entities: ParsedEntity[];
  filters?: SearchRequest;
}

export interface ParsedEntity {
  type: string;
  value: string;
}