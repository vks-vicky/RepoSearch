package com.example.reposearch.repository;

import com.example.reposearch.entity.GithubRepositoryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PostgresGithubRepositoryUpsertRepositoryIntegrationTest {

    private static final Long TEST_GITHUB_REPOSITORY_ID = 9_999_999_999L;

    @Autowired
    private GithubRepositoryUpsertRepository upsertRepository;

    @Autowired
    private GithubRepositoryJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void removeTestRepository() {
        jpaRepository.findByGithubRepositoryId(TEST_GITHUB_REPOSITORY_ID)
                .ifPresent(jpaRepository::delete);
    }

    @Test
    void upsertAllShouldInsertThenUpdateExistingRepositoryWithoutDuplicates() {
        GithubRepositoryEntity originalRepository = new GithubRepositoryEntity(
                TEST_GITHUB_REPOSITORY_ID,
                "repo-search",
                "Original description",
                "vivek",
                "Java",
                100L,
                10L,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        upsertRepository.upsertAll(List.of(originalRepository));

        GithubRepositoryEntity insertedRepository = jpaRepository
                .findByGithubRepositoryId(TEST_GITHUB_REPOSITORY_ID)
                .orElseThrow();

        assertNotNull(insertedRepository.getId());
        assertEquals("Original description", insertedRepository.getDescription());
        assertEquals(100L, insertedRepository.getStarsCount());

        GithubRepositoryEntity updatedRepository = new GithubRepositoryEntity(
                TEST_GITHUB_REPOSITORY_ID,
                "repo-search",
                "Updated description",
                "vivek",
                "Java",
                250L,
                25L,
                Instant.parse("2026-02-01T10:00:00Z")
        );

        upsertRepository.upsertAll(List.of(updatedRepository));

        GithubRepositoryEntity repositoryAfterUpsert = jpaRepository
                .findByGithubRepositoryId(TEST_GITHUB_REPOSITORY_ID)
                .orElseThrow();

        Integer matchingRowCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM github_repositories
                WHERE github_repository_id = ?
                """,
                Integer.class,
                TEST_GITHUB_REPOSITORY_ID
        );

        assertEquals(1, matchingRowCount);
        assertEquals(insertedRepository.getId(), repositoryAfterUpsert.getId());
        assertEquals("Updated description", repositoryAfterUpsert.getDescription());
        assertEquals(250L, repositoryAfterUpsert.getStarsCount());
        assertEquals(25L, repositoryAfterUpsert.getForksCount());
    }
}