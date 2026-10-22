CREATE TABLE github_repositories (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     github_repository_id BIGINT NOT NULL UNIQUE,
                                     name VARCHAR(500) NOT NULL,
                                     description TEXT,
                                     owner_name VARCHAR(255) NOT NULL,
                                     language VARCHAR(100),
                                     stars_count BIGINT NOT NULL DEFAULT 0 CHECK (stars_count >= 0),
                                     forks_count BIGINT NOT NULL DEFAULT 0 CHECK (forks_count >= 0),
                                     last_updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_github_repositories_language
    ON github_repositories (language);

CREATE INDEX idx_github_repositories_stars_count
    ON github_repositories (stars_count);

CREATE INDEX idx_github_repositories_forks_count
    ON github_repositories (forks_count);

CREATE INDEX idx_github_repositories_last_updated_at
    ON github_repositories (last_updated_at);