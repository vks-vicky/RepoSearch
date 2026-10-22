package com.example.reposearch.exception;

public class GithubRateLimitException extends GithubApiException {

    public GithubRateLimitException(String message) {
        super(message);
    }
}