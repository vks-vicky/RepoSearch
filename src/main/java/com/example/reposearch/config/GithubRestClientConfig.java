package com.example.reposearch.config;

import com.example.reposearch.config.properties.GithubApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class GithubRestClientConfig {

    @Bean("githubRestClient")
    public RestClient githubRestClient(
            RestClient.Builder builder,
            GithubApiProperties properties
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "RepoSearch")
                .requestInterceptor((request, body, execution) -> {
                    if (StringUtils.hasText(properties.token())) {
                        request.getHeaders().setBearerAuth(properties.token());
                    }

                    return execution.execute(request, body);
                })
                .build();
    }
}