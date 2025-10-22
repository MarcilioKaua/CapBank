package com.capbank.transaction_service.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record TransactionHistoryPageResponse(
        @JsonProperty("content")
        List<TransactionHistoryResponse> content,

        @JsonProperty("page_number")
        int pageNumber,

        @JsonProperty("page_size")
        int pageSize,

        @JsonProperty("total_elements")
        long totalElements,

        @JsonProperty("total_pages")
        int totalPages,

        @JsonProperty("first")
        boolean first,

        @JsonProperty("last")
        boolean last
) {}