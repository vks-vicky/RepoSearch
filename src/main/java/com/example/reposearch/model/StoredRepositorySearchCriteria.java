package com.example.reposearch.model;

public record StoredRepositorySearchCriteria(
        String language,
        Long minStars,
        RepositorySortField sortField,
        SortDirection direction,
        int page,
        int size
) {
}