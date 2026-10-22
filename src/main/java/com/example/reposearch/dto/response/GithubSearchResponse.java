package com.example.reposearch.dto.response;

import java.util.List;

public record GithubSearchResponse(
        String message,
        List<GithubRepositoryResponse> repositories
) {
}