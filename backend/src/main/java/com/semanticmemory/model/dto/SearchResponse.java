package com.semanticmemory.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    private List<SearchResult> results;
    private long total;
    private int page;
    private int pageSize;
    private boolean hasMore;
    private QueryAnalysis queryAnalysis;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResult {
        private AssetResponse asset;
        private float score;
        private List<String> matchedEntities;
        private String highlightedText;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssetResponse {
        private String id;
        private String fileName;
        private String fileType;
        private String filePath;
        private Long fileSizeBytes;
        private String modifiedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueryAnalysis {
        private String intent;
        private List<ParsedEntity> entities;
        private SearchRequest filters;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedEntity {
        private String type;
        private String value;
    }
}