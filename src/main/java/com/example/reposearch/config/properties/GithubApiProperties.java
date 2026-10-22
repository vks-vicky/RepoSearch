package com.example.reposearch.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "github.api")
public record GithubApiProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout,
        String token
) {
}