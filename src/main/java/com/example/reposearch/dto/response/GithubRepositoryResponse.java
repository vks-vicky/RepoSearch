package com.example.reposearch.dto.response;

import java.time.Instant;

public record GithubRepositoryResponse(
        Long id,
        String name,
        String description,
        String owner,
        String language,
        Long stars,
        Long forks,
        Instant lastUpdated
) {
}