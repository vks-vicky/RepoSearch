package com.example.reposearch.client;

import com.example.reposearch.client.dto.GithubSearchApiResponse;
import com.example.reposearch.exception.GithubApiException;
import com.example.reposearch.exception.GithubRateLimitException;
import com.example.reposearch.model.GithubSearchCriteria;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestClientGithubApiClient implements GithubApiClient {

    private final RestClient githubRestClient;

    public RestClientGithubApiClient(
            @Qualifier("githubRestClient") RestClient githubRestClient
    ) {
        this.githubRestClient = githubRestClient;
    }

    @Override
    public GithubSearchApiResponse searchRepositories(
            GithubSearchCriteria criteria
    ) {
        try {
            GithubSearchApiResponse response = githubRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/repositories")
                            .queryParam("q", buildSearchQuery(criteria))
                            .queryParam(
                                    "sort",
                                    criteria.sortField().getGithubApiValue()
                            )
                            .queryParam(
                                    "order",
                                    criteria.direction().getRequestValue()
                            )
                            .build())
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 403
                                    || status.value() == 429,
                            (request, clientResponse) -> {
                                throw new GithubRateLimitException(
                                        "GitHub API rate limit has been reached"
                                );
                            }
                    )
                    .onStatus(
                            status -> status.isError(),
                            (request, clientResponse) -> {
                                throw new GithubApiException(
                                        "GitHub API request failed with status: "
                                                + clientResponse.getStatusCode().value()
                                );
                            }
                    )
                    .body(GithubSearchApiResponse.class);

            if (response == null) {
                throw new GithubApiException(
                        "GitHub API returned an empty response"
                );
            }

            return response;

        } catch (GithubApiException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new GithubApiException(
                    "GitHub API is unavailable or returned an invalid response",
                    exception
            );
        }
    }

    private String buildSearchQuery(GithubSearchCriteria criteria) {
        String query = criteria.query().trim();

        if (StringUtils.hasText(criteria.language())) {
            query += " language:" + criteria.language().trim();
        }

        return query;
    }
}