package com.example.reposearch.service.impl;

import com.example.reposearch.client.GithubApiClient;
import com.example.reposearch.client.dto.GithubSearchApiResponse;
import com.example.reposearch.entity.GithubRepositoryEntity;
import com.example.reposearch.exception.GithubApiException;
import com.example.reposearch.mapper.GithubRepositoryMapper;
import com.example.reposearch.model.GithubSearchCriteria;
import com.example.reposearch.repository.GithubRepositoryJpaRepository;
import com.example.reposearch.repository.GithubRepositoryUpsertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceImplTest {

    @Mock
    private GithubApiClient githubApiClient;

    @Mock
    private GithubRepositoryMapper repositoryMapper;

    @Mock
    private GithubRepositoryUpsertRepository upsertRepository;

    @Mock
    private GithubRepositoryJpaRepository jpaRepository;

    @InjectMocks
    private GithubRepositoryServiceImpl githubRepositoryService;

    @Test
    void searchAndStoreShouldFetchMapUpsertAndReturnRepositories() {
        GithubSearchCriteria criteria = createCriteria();

        GithubSearchApiResponse githubResponse = new GithubSearchApiResponse(
                1,
                false,
                List.of()
        );

        GithubRepositoryEntity mappedRepository = createRepository();
        List<GithubRepositoryEntity> mappedRepositories = List.of(mappedRepository);

        when(githubApiClient.searchRepositories(criteria))
                .thenReturn(githubResponse);

        when(repositoryMapper.toEntities(githubResponse.items()))
                .thenReturn(mappedRepositories);

        List<GithubRepositoryEntity> result =
                githubRepositoryService.searchAndStore(criteria);

        assertEquals(mappedRepositories, result);

        InOrder inOrder = inOrder(
                githubApiClient,
                repositoryMapper,
                upsertRepository
        );

        inOrder.verify(githubApiClient).searchRepositories(criteria);
        inOrder.verify(repositoryMapper).toEntities(githubResponse.items());
        inOrder.verify(upsertRepository).upsertAll(mappedRepositories);
    }

    @Test
    void searchAndStoreShouldReturnEmptyListWhenGithubReturnsNoRepositories() {
        GithubSearchCriteria criteria = createCriteria();

        GithubSearchApiResponse githubResponse = new GithubSearchApiResponse(
                0,
                false,
                List.of()
        );

        when(githubApiClient.searchRepositories(criteria))
                .thenReturn(githubResponse);

        when(repositoryMapper.toEntities(githubResponse.items()))
                .thenReturn(List.of());

        List<GithubRepositoryEntity> result =
                githubRepositoryService.searchAndStore(criteria);

        assertTrue(result.isEmpty());

        InOrder inOrder = inOrder(
                githubApiClient,
                repositoryMapper,
                upsertRepository
        );

        inOrder.verify(githubApiClient).searchRepositories(criteria);
        inOrder.verify(repositoryMapper).toEntities(githubResponse.items());
        inOrder.verify(upsertRepository).upsertAll(List.of());
    }

    @Test
    void searchAndStoreShouldPropagateGithubApiException() {
        GithubSearchCriteria criteria = createCriteria();

        GithubApiException githubApiException =
                new GithubApiException("GitHub API is unavailable");

        when(githubApiClient.searchRepositories(criteria))
                .thenThrow(githubApiException);

        GithubApiException thrownException = assertThrows(
                GithubApiException.class,
                () -> githubRepositoryService.searchAndStore(criteria)
        );

        assertSame(githubApiException, thrownException);
        verifyNoInteractions(repositoryMapper, upsertRepository);
    }

    private GithubSearchCriteria createCriteria() {
        return new GithubSearchCriteria(
                "spring boot",
                "Java",
                null,
                null
        );
    }

    private GithubRepositoryEntity createRepository() {
        return new GithubRepositoryEntity(
                9_999_999_990L,
                "repo-search-test",
                "Service test repository",
                "test-owner",
                "Java",
                100L,
                10L,
                Instant.parse("2026-01-01T10:00:00Z")
        );
    }
}