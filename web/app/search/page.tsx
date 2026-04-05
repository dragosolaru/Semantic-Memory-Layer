'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { useAuth } from '@/lib/auth';
import { api } from '@/lib/api';
import { SearchResult, SearchResponse } from '@/lib/types';

export default function SearchPage() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/login');
    }
  }, [user, authLoading, router]);

  const handleSearch = async (e: React.FormEvent) => {
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

  if (authLoading) {
    return <div className="loading">Loading...</div>;
  }

  return (
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
  );
}