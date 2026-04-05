package com.semanticmemory.controller;

import com.semanticmemory.model.dto.*;
import com.semanticmemory.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Search controller for semantic memory search.
 * 
 * All endpoints require JWT authentication.
 * User ID is extracted from JWT claims (not from headers).
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    /**
     * Search semantic memory.
     * 
     * User ID is extracted from JWT authentication.
     * This is more secure than using a header which can be spoofed.
     * 
     * @param request Search parameters
     * @return Search results with relevance scores
     */
    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request) {
        // Get user ID from JWT - more secure than header
        String userId = getCurrentUserId();
        
        return ResponseEntity.ok(searchService.search(request, userId));
    }

    /**
     * Get current authenticated user ID from JWT.
     * 
     * @return User ID from security context
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}