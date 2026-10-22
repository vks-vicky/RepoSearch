package com.example.reposearch.client;

import com.example.reposearch.client.dto.GithubSearchApiResponse;
import com.example.reposearch.model.GithubSearchCriteria;

public interface GithubApiClient {

    GithubSearchApiResponse searchRepositories(
            GithubSearchCriteria criteria
    );
}