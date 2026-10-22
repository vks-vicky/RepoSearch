package com.example.reposearch.controller;

import com.example.reposearch.dto.request.GithubSearchRequest;
import com.example.reposearch.dto.response.GithubRepositoryResponse;
import com.example.reposearch.dto.response.GithubSearchResponse;
import com.example.reposearch.mapper.GithubRepositoryResponseMapper;
import com.example.reposearch.model.GithubSearchCriteria;
import com.example.reposearch.model.RepositorySortField;
import com.example.reposearch.model.SortDirection;
import com.example.reposearch.service.GithubRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@Tag(name = "GitHub Repositories")
public class GithubRepositoryController {

    private final GithubRepositoryService githubRepositoryService;
    private final GithubRepositoryResponseMapper responseMapper;

    public GithubRepositoryController(
            GithubRepositoryService githubRepositoryService,
            GithubRepositoryResponseMapper responseMapper
    ) {
        this.githubRepositoryService = githubRepositoryService;
        this.responseMapper = responseMapper;
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search GitHub repositories and store the results"
    )
    public ResponseEntity<GithubSearchResponse> searchRepositories(
            @Valid @RequestBody GithubSearchRequest request
    ) {
        GithubSearchCriteria criteria = toCriteria(request);

        List<GithubRepositoryResponse> repositories =
                responseMapper.toResponses(
                        githubRepositoryService.searchAndStore(criteria)
                );

        return ResponseEntity.ok(
                new GithubSearchResponse(
                        "Repositories fetched and saved successfully",
                        repositories
                )
        );
    }

    private GithubSearchCriteria toCriteria(
            GithubSearchRequest request
    ) {
        RepositorySortField sortField = StringUtils.hasText(request.sort())
                ? RepositorySortField.fromRequestValue(request.sort())
                : RepositorySortField.STARS;

        SortDirection direction = StringUtils.hasText(request.direction())
                ? SortDirection.fromRequestValue(request.direction())
                : SortDirection.DESC;

        return new GithubSearchCriteria(
                request.query(),
                request.language(),
                sortField,
                direction
        );
    }
}