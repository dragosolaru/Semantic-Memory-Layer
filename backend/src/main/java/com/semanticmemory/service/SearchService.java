package com.semanticmemory.service;

import com.semanticmemory.model.dto.SearchRequest;
import com.semanticmemory.model.dto.SearchResponse;
import com.semanticmemory.model.entity.Asset;
import com.semanticmemory.repository.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SearchService {
    
    private final AssetRepository assetRepository;
    
    public SearchService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
    
    public SearchResponse search(SearchRequest request, String userId) {
        SearchResponse.QueryAnalysis queryAnalysis = parseQuery(request.getQuery());
        
        Page<Asset> results = assetRepository.findByUserId(
            UUID.fromString(userId),
            PageRequest.of(request.getPage(), request.getPageSize())
        );
        
        List<SearchResponse.SearchResult> searchResults = new ArrayList<>();
        for (Asset asset : results.getContent()) {
            searchResults.add(SearchResponse.SearchResult.builder()
                .asset(SearchResponse.AssetResponse.builder()
                    .id(asset.getId().toString())
                    .fileName(asset.getFileName())
                    .fileType(asset.getFileType().name())
                    .filePath(asset.getFilePath())
                    .fileSizeBytes(asset.getFileSizeBytes())
                    .modifiedAt(asset.getModifiedAt().toString())
                    .build())
                .score(0.95f)
                .matchedEntities(Collections.emptyList())
                .build());
        }
        
        return SearchResponse.builder()
            .results(searchResults)
            .total(results.getTotalElements())
            .page(results.getNumber())
            .pageSize(results.getSize())
            .hasMore(results.hasNext())
            .queryAnalysis(queryAnalysis)
            .build();
    }
    
    private SearchResponse.QueryAnalysis parseQuery(String query) {
        List<SearchResponse.ParsedEntity> entities = new ArrayList<>();
        String intent = "search";
        
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("photo") || lowerQuery.contains("image") || lowerQuery.contains("picture")) {
            entities.add(new SearchResponse.ParsedEntity("type", "IMAGE"));
            intent = "find_photos";
        }
        if (lowerQuery.contains("invoice") || lowerQuery.contains("bill") || lowerQuery.contains("receipt")) {
            entities.add(new SearchResponse.ParsedEntity("type", "DOCUMENT"));
            intent = "find_documents";
        }
        
        return SearchResponse.QueryAnalysis.builder()
            .intent(intent)
            .entities(entities)
            .filters(null)
            .build();
    }
}