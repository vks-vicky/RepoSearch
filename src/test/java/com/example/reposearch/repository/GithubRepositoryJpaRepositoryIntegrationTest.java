package com.example.reposearch.repository;

import com.example.reposearch.entity.GithubRepositoryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class GithubRepositoryJpaRepositoryIntegrationTest {

    private static final Long JAVA_HIGH_STARS_ID = 9_999_999_901L;
    private static final Long JAVA_LOW_STARS_ID = 9_999_999_902L;
    private static final Long KOTLIN_HIGH_STARS_ID = 9_999_999_903L;

    @Autowired
    private GithubRepositoryJpaRepository jpaRepository;

    @Autowired
    private GithubRepositoryUpsertRepository upsertRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        removeTestRows();

        upsertRepository.upsertAll(List.of(
                createRepository(
                        JAVA_HIGH_STARS_ID,
                        "java-high-stars",
                        "Java",
                        500L
                ),
                createRepository(
                        JAVA_LOW_STARS_ID,
                        "java-low-stars",
                        "Java",
                        100L
                ),
                createRepository(
                        KOTLIN_HIGH_STARS_ID,
                        "kotlin-high-stars",
                        "Kotlin",
                        800L
                )
        ));
    }

    @AfterEach
    void cleanUp() {
        removeTestRows();
    }

    @Test
    void findAllShouldApplyLanguageAndMinimumStarsFilters() {
        Page<GithubRepositoryEntity> result = jpaRepository.findAll(
                GithubRepositorySpecifications.withFilters("Java", 200L),
                PageRequest.of(0, 20)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(
                JAVA_HIGH_STARS_ID,
                result.getContent().getFirst().getGithubRepositoryId()
        );
    }

    @Test
    void findAllShouldFilterUsingOnlyMinimumStarsWhenLanguageIsNotProvided() {
        Page<GithubRepositoryEntity> result = jpaRepository.findAll(
                GithubRepositorySpecifications.withFilters(null, 400L),
                PageRequest.of(0, 20)
        );

        List<Long> repositoryIds = result.getContent().stream()
                .map(GithubRepositoryEntity::getGithubRepositoryId)
                .toList();

        assertEquals(2, result.getTotalElements());
        assertEquals(true, repositoryIds.contains(JAVA_HIGH_STARS_ID));
        assertEquals(true, repositoryIds.contains(KOTLIN_HIGH_STARS_ID));
    }

    @Test
    void findAllShouldPaginateAndSortByStarsDescending() {
        Page<GithubRepositoryEntity> firstPage = jpaRepository.findAll(
                GithubRepositorySpecifications.withFilters(null, null),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "starsCount"))
        );

        assertEquals(3, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());
        assertEquals(KOTLIN_HIGH_STARS_ID,
                firstPage.getContent().getFirst().getGithubRepositoryId());
        assertEquals(JAVA_HIGH_STARS_ID,
                firstPage.getContent().get(1).getGithubRepositoryId());
    }

    private GithubRepositoryEntity createRepository(
            Long githubRepositoryId,
            String name,
            String language,
            Long starsCount
    ) {
        return new GithubRepositoryEntity(
                githubRepositoryId,
                name,
                "Test repository description",
                "test-owner",
                language,
                starsCount,
                10L,
                Instant.parse("2026-01-01T10:00:00Z")
        );
    }

    private void removeTestRows() {
        jdbcTemplate.update(
                "DELETE FROM github_repositories WHERE github_repository_id = ?",
                JAVA_HIGH_STARS_ID
        );
        jdbcTemplate.update(
                "DELETE FROM github_repositories WHERE github_repository_id = ?",
                JAVA_LOW_STARS_ID
        );
        jdbcTemplate.update(
                "DELETE FROM github_repositories WHERE github_repository_id = ?",
                KOTLIN_HIGH_STARS_ID
        );
    }
}