package com.example.reposearch.service.impl;

import com.example.reposearch.client.GithubApiClient;
import com.example.reposearch.client.dto.GithubSearchApiResponse;
import com.example.reposearch.entity.GithubRepositoryEntity;
import com.example.reposearch.mapper.GithubRepositoryMapper;
import com.example.reposearch.model.GithubSearchCriteria;
import com.example.reposearch.model.SortDirection;
import com.example.reposearch.model.StoredRepositorySearchCriteria;
import com.example.reposearch.repository.GithubRepositoryJpaRepository;
import com.example.reposearch.repository.GithubRepositorySpecifications;
import com.example.reposearch.repository.GithubRepositoryUpsertRepository;
import com.example.reposearch.service.GithubRepositoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GithubRepositoryServiceImpl
        implements GithubRepositoryService {

    private final GithubApiClient githubApiClient;
    private final GithubRepositoryMapper repositoryMapper;
    private final GithubRepositoryUpsertRepository upsertRepository;
    private final GithubRepositoryJpaRepository jpaRepository;

    public GithubRepositoryServiceImpl(
            GithubApiClient githubApiClient,
            GithubRepositoryMapper repositoryMapper,
            GithubRepositoryUpsertRepository upsertRepository,
            GithubRepositoryJpaRepository jpaRepository
    ) {
        this.githubApiClient = githubApiClient;
        this.repositoryMapper = repositoryMapper;
        this.upsertRepository = upsertRepository;
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public List<GithubRepositoryEntity> searchAndStore(
            GithubSearchCriteria criteria
    ) {
        GithubSearchApiResponse githubResponse =
                githubApiClient.searchRepositories(criteria);

        List<GithubRepositoryEntity> repositories =
                repositoryMapper.toEntities(githubResponse.items());

        upsertRepository.upsertAll(repositories);

        return repositories;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GithubRepositoryEntity> findStoredRepositories(
            StoredRepositorySearchCriteria criteria
    ) {
        Sort.Direction direction =
                criteria.direction() == SortDirection.ASC
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        PageRequest pageRequest = PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(
                        direction,
                        criteria.sortField().getDatabaseProperty()
                )
        );

        return jpaRepository.findAll(
                GithubRepositorySpecifications.withFilters(
                        criteria.language(),
                        criteria.minStars()
                ),
                pageRequest
        );
    }
}