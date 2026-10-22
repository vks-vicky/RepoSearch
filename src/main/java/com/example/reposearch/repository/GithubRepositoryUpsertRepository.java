package com.example.reposearch.repository;

import com.example.reposearch.entity.GithubRepositoryEntity;

import java.util.List;

public interface GithubRepositoryUpsertRepository {

    void upsertAll(List<GithubRepositoryEntity> repositories);
}