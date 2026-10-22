package com.example.reposearch.service;

import com.example.reposearch.entity.GithubRepositoryEntity;
import com.example.reposearch.model.GithubSearchCriteria;
import com.example.reposearch.model.StoredRepositorySearchCriteria;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GithubRepositoryService {

    List<GithubRepositoryEntity> searchAndStore(
            GithubSearchCriteria criteria
    );

    Page<GithubRepositoryEntity> findStoredRepositories(
            StoredRepositorySearchCriteria criteria
    );
}