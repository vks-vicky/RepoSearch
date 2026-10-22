package com.example.reposearch.controller;

import com.example.reposearch.mapper.GithubRepositoryResponseMapper;
import com.example.reposearch.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.reposearch.dto.response.GithubRepositoryResponse;
import com.example.reposearch.entity.GithubRepositoryEntity;
import com.example.reposearch.model.GithubSearchCriteria;
import com.example.reposearch.model.RepositorySortField;
import com.example.reposearch.model.SortDirection;
import com.example.reposearch.model.StoredRepositorySearchCriteria;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubRepositoryController.class)
class GithubRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubRepositoryService githubRepositoryService;

    @MockitoBean
    private GithubRepositoryResponseMapper repositoryResponseMapper;

    @Test
    void searchRepositoriesShouldReturnBadRequestWhenQueryIsBlank() throws Exception {
        String requestBody = """
                {
                  "query": " ",
                  "language": "Java",
                  "sort": "stars",
                  "direction": "desc"
                }
                """;

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getStoredRepositoriesShouldReturnBadRequestWhenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/github/repositories")
                        .param("size", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void getStoredRepositoriesShouldReturnBadRequestWhenMinStarsIsNegative() throws Exception {
        mockMvc.perform(get("/api/github/repositories")
                        .param("minStars", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchRepositoriesShouldReturnMappedSuccessfulResponse() throws Exception {
        GithubRepositoryEntity repository = new GithubRepositoryEntity(
                101L,
                "spring-project",
                "Spring Boot project",
                "spring-projects",
                "Java",
                500L,
                50L,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        GithubRepositoryResponse repositoryResponse =
                new GithubRepositoryResponse(
                        101L,
                        "spring-project",
                        "Spring Boot project",
                        "spring-projects",
                        "Java",
                        500L,
                        50L,
                        Instant.parse("2026-01-01T10:00:00Z")
                );

        when(githubRepositoryService.searchAndStore(any(GithubSearchCriteria.class)))
                .thenReturn(List.of(repository));

        when(repositoryResponseMapper.toResponses(List.of(repository)))
                .thenReturn(List.of(repositoryResponse));

        String requestBody = """
            {
              "query": "spring boot",
              "language": "Java",
              "sort": "stars",
              "direction": "desc"
            }
            """;

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories[0].id").value(101))
                .andExpect(jsonPath("$.repositories[0].name")
                        .value("spring-project"))
                .andExpect(jsonPath("$.repositories[0].stars").value(500));

        ArgumentCaptor<GithubSearchCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(GithubSearchCriteria.class);

        verify(githubRepositoryService)
                .searchAndStore(criteriaCaptor.capture());

        GithubSearchCriteria capturedCriteria = criteriaCaptor.getValue();

        assertEquals("spring boot", capturedCriteria.query());
        assertEquals("Java", capturedCriteria.language());
        assertEquals(RepositorySortField.STARS, capturedCriteria.sortField());
        assertEquals(SortDirection.DESC, capturedCriteria.direction());
    }

    @Test
    void getStoredRepositoriesShouldReturnPagedSuccessfulResponse() throws Exception {
        GithubRepositoryEntity repository = new GithubRepositoryEntity(
                202L,
                "stored-repository",
                "Stored repository",
                "test-owner",
                "Java",
                300L,
                30L,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        GithubRepositoryResponse repositoryResponse =
                new GithubRepositoryResponse(
                        202L,
                        "stored-repository",
                        "Stored repository",
                        "test-owner",
                        "Java",
                        300L,
                        30L,
                        Instant.parse("2026-01-01T10:00:00Z")
                );

        Page<GithubRepositoryEntity> repositoryPage = new PageImpl<>(
                List.of(repository),
                PageRequest.of(1, 2),
                5
        );

        when(githubRepositoryService.findStoredRepositories(
                any(StoredRepositorySearchCriteria.class)
        )).thenReturn(repositoryPage);

        when(repositoryResponseMapper.toResponses(List.of(repository)))
                .thenReturn(List.of(repositoryResponse));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "50")
                        .param("sort", "forks")
                        .param("direction", "asc")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].id").value(202))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true));

        ArgumentCaptor<StoredRepositorySearchCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(StoredRepositorySearchCriteria.class);

        verify(githubRepositoryService)
                .findStoredRepositories(criteriaCaptor.capture());

        StoredRepositorySearchCriteria capturedCriteria =
                criteriaCaptor.getValue();

        assertEquals("Java", capturedCriteria.language());
        assertEquals(50L, capturedCriteria.minStars());
        assertEquals(RepositorySortField.FORKS, capturedCriteria.sortField());
        assertEquals(SortDirection.ASC, capturedCriteria.direction());
        assertEquals(1, capturedCriteria.page());
        assertEquals(2, capturedCriteria.size());
    }
}