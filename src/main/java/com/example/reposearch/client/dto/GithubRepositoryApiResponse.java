package com.example.reposearch.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryApiResponse(
        Long id,
        String name,
        String description,
        GithubOwnerApiResponse owner,
        String language,

        @JsonProperty("stargazers_count")
        Long starsCount,

        @JsonProperty("forks_count")
        Long forksCount,

        @JsonProperty("updated_at")
        Instant lastUpdatedAt
) {
}