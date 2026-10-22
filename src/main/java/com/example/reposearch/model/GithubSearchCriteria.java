package com.example.reposearch.model;

public record GithubSearchCriteria(
        String query,
        String language,
        RepositorySortField sortField,
        SortDirection direction
) {
}