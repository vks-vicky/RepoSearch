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
import com.example.reposearch.dto.response.PagedRepositoryResponse;
import com.example.reposearch.dto.response.RepositorySortResponse;
import com.example.reposearch.entity.GithubRepositoryEntity;
import com.example.reposearch.model.StoredRepositorySearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private void validatePageRequest(
            Long minStars,
            int page,
            int size
    ) {
        if (minStars != null && minStars < 0) {
            throw new IllegalArgumentException(
                    "minStars must be greater than or equal to zero"
            );
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "page must be greater than or equal to zero"
            );
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                    "size must be between 1 and 50"
            );
        }
    }

    @GetMapping("/repositories")
    @Operation(
            summary = "Retrieve stored repositories with filters, sorting, and pagination"
    )
    public ResponseEntity<PagedRepositoryResponse> getStoredRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Long minStars,
            @RequestParam(defaultValue = "stars") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        validatePageRequest(minStars, page, size);

        RepositorySortField sortField =
                RepositorySortField.fromRequestValue(sort);

        SortDirection sortDirection =
                SortDirection.fromRequestValue(direction);

        StoredRepositorySearchCriteria criteria =
                new StoredRepositorySearchCriteria(
                        language,
                        minStars,
                        sortField,
                        sortDirection,
                        page,
                        size
                );

        Page<GithubRepositoryEntity> repositoryPage =
                githubRepositoryService.findStoredRepositories(criteria);

        PagedRepositoryResponse response =
                new PagedRepositoryResponse(
                        responseMapper.toResponses(repositoryPage.getContent()),
                        repositoryPage.getNumber(),
                        repositoryPage.getSize(),
                        repositoryPage.getTotalElements(),
                        repositoryPage.getTotalPages(),
                        repositoryPage.hasNext(),
                        new RepositorySortResponse(
                                sortField.getRequestValue(),
                                sortDirection.name()
                        )
                );

        return ResponseEntity.ok(response);
    }
}