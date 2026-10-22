package com.example.reposearch.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubOwnerApiResponse(
        String login
) {
}