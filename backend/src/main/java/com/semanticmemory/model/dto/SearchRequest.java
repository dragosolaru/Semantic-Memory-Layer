package com.semanticmemory.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchRequest {
    private String query;
    private List<String> assetType;
    private String sourceId;
    private String dateFrom;
    private String dateTo;
    private String location;
    private Integer page = 0;
    private Integer pageSize = 20;
}