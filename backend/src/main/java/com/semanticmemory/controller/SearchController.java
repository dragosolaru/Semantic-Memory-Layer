package com.semanticmemory.controller;

import com.semanticmemory.model.dto.*;
import com.semanticmemory.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(searchService.search(request, userId));
    }
}