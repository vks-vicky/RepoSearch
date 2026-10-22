package com.example.reposearch.repository;

import com.example.reposearch.entity.GithubRepositoryEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PostgresGithubRepositoryUpsertRepository
        implements GithubRepositoryUpsertRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO github_repositories (
                github_repository_id,
                name,
                description,
                owner_name,
                language,
                stars_count,
                forks_count,
                last_updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (github_repository_id)
            DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                owner_name = EXCLUDED.owner_name,
                language = EXCLUDED.language,
                stars_count = EXCLUDED.stars_count,
                forks_count = EXCLUDED.forks_count,
                last_updated_at = EXCLUDED.last_updated_at,
                updated_at = CURRENT_TIMESTAMP
            """;

    private final JdbcTemplate jdbcTemplate;

    public PostgresGithubRepositoryUpsertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void upsertAll(List<GithubRepositoryEntity> repositories) {
        if (repositories.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(UPSERT_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement statement, int index)
                    throws SQLException {

                GithubRepositoryEntity repository = repositories.get(index);

                statement.setLong(1, repository.getGithubRepositoryId());
                statement.setString(2, repository.getName());
                statement.setString(3, repository.getDescription());
                statement.setString(4, repository.getOwnerName());
                statement.setString(5, repository.getLanguage());
                statement.setLong(6, repository.getStarsCount());
                statement.setLong(7, repository.getForksCount());
                statement.setTimestamp(
                        8,
                        Timestamp.from(repository.getLastUpdatedAt())
                );
            }

            @Override
            public int getBatchSize() {
                return repositories.size();
            }
        });
    }
}