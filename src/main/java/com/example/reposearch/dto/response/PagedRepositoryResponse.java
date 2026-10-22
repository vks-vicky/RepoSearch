package com.example.reposearch.dto.response;

import java.util.List;

public record PagedRepositoryResponse(
        List<GithubRepositoryResponse> repositories,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        RepositorySortResponse sort
) {
}