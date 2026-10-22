package com.example.reposearch.mapper;

import com.example.reposearch.client.dto.GithubRepositoryApiResponse;
import com.example.reposearch.entity.GithubRepositoryEntity;
import com.example.reposearch.exception.GithubApiException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class GithubRepositoryMapper {

    public List<GithubRepositoryEntity> toEntities(
            List<GithubRepositoryApiResponse> repositories
    ) {
        if (repositories == null || repositories.isEmpty()) {
            return List.of();
        }

        return repositories.stream()
                .map(this::toEntity)
                .toList();
    }

    private GithubRepositoryEntity toEntity(
            GithubRepositoryApiResponse repository
    ) {
        if (repository == null
                || repository.id() == null
                || !StringUtils.hasText(repository.name())
                || repository.owner() == null
                || !StringUtils.hasText(repository.owner().login())
                || repository.lastUpdatedAt() == null) {

            throw new GithubApiException(
                    "GitHub API returned an invalid repository payload"
            );
        }

        return new GithubRepositoryEntity(
                repository.id(),
                repository.name(),
                repository.description(),
                repository.owner().login(),
                repository.language(),
                repository.starsCount() == null ? 0L : repository.starsCount(),
                repository.forksCount() == null ? 0L : repository.forksCount(),
                repository.lastUpdatedAt()
        );
    }
}