package com.example.reposearch.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubSearchApiResponse(

        @JsonProperty("total_count")
        Integer totalCount,

        @JsonProperty("incomplete_results")
        Boolean incompleteResults,

        List<GithubRepositoryApiResponse> items
) {
}