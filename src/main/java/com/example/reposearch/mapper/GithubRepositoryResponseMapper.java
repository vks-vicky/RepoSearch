package com.example.reposearch.mapper;

import com.example.reposearch.dto.response.GithubRepositoryResponse;
import com.example.reposearch.entity.GithubRepositoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubRepositoryResponseMapper {

    public List<GithubRepositoryResponse> toResponses(
            List<GithubRepositoryEntity> repositories
    ) {
        return repositories.stream()
                .map(this::toResponse)
                .toList();
    }

    private GithubRepositoryResponse toResponse(
            GithubRepositoryEntity repository
    ) {
        return new GithubRepositoryResponse(
                repository.getGithubRepositoryId(),
                repository.getName(),
                repository.getDescription(),
                repository.getOwnerName(),
                repository.getLanguage(),
                repository.getStarsCount(),
                repository.getForksCount(),
                repository.getLastUpdatedAt()
        );
    }
}