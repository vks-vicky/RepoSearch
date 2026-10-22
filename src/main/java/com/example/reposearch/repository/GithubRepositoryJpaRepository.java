package com.example.reposearch.repository;

import com.example.reposearch.entity.GithubRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GithubRepositoryJpaRepository
        extends JpaRepository<GithubRepositoryEntity, Long>,
        JpaSpecificationExecutor<GithubRepositoryEntity> {

    Optional<GithubRepositoryEntity> findByGithubRepositoryId(Long githubRepositoryId);
}