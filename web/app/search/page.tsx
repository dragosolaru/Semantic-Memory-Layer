/**
 * Search Page
 * 
 * Semantic search interface for querying user memories.
 * Requires authentication to access.
 * 
 * @module app/search/page
 */

'use client';

import { useState, FormEvent } from 'react';
import { api } from '@/lib/api';
import { SearchResult, SearchResponse } from '@/lib/types';
import ProtectedRoute from '../ProtectedRoute';

/**
 * SearchPage Component
 * 
 * Renders search form and displays results with relevance scores.
 * Supports:
 * - Text search query
 * - Relevance scoring (0-100%)
 * - Highlighted text matches
 * - File type display
 * 
 * @returns {JSX.Element} Search page
 */
export default function SearchPage() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  /**
   * Handle search submission
   * @param {FormEvent} e - Form submit event
   */
  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;

    setIsLoading(true);
    setHasSearched(true);

    try {
      const response: SearchResponse = await api.search({ query });
      setResults(response.results || []);
    } catch (err) {
      console.error('Search failed:', err);
      setResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <ProtectedRoute>
      <div className="search-page">
        <h1>Search Your Memory</h1>
        <form onSubmit={handleSearch} className="search-form">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="What are you looking for?"
            className="search-input"
          />
          <button type="submit" disabled={isLoading} className="btn-primary">
            {isLoading ? 'Searching...' : 'Search'}
          </button>
        </form>

        {hasSearched && (
          <div className="search-results">
            {results.length > 0 ? (
              <>
                <p className="results-count">Found {results.length} results</p>
                <div className="results-list">
                  {results.map((result) => (
                    <div key={result.asset.id} className="result-card">
                      <h3>{result.asset.fileName}</h3>
                      <p className="file-type">{result.asset.fileType}</p>
                      {result.highlightedText && (
                        <p className="highlighted-text">{result.highlightedText}</p>
                      )}
                      <span className="relevance-score">
                        Relevance: {Math.round(result.score * 100)}%
                      </span>
                    </div>
                  ))}
                </div>
              </>
            ) : (
              <p className="no-results">No results found. Try a different search.</p>
            )}
          </div>
        )}
      </div>
    </ProtectedRoute>
  );
}