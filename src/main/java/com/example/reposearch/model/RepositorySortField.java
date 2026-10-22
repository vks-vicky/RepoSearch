package com.example.reposearch.model;

import java.util.Arrays;

public enum RepositorySortField {

    STARS("stars", "starsCount", "stars"),
    FORKS("forks", "forksCount", "forks"),
    UPDATED("updated", "lastUpdatedAt", "updated");

    private final String requestValue;
    private final String databaseProperty;
    private final String githubApiValue;

    RepositorySortField(
            String requestValue,
            String databaseProperty,
            String githubApiValue
    ) {
        this.requestValue = requestValue;
        this.databaseProperty = databaseProperty;
        this.githubApiValue = githubApiValue;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public String getDatabaseProperty() {
        return databaseProperty;
    }

    public String getGithubApiValue() {
        return githubApiValue;
    }

    public static RepositorySortField fromRequestValue(String value) {
        return Arrays.stream(values())
                .filter(sortField ->
                        sortField.requestValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported sort field: " + value
                        ));
    }
}