package com.example.reposearch.controller;

import com.example.reposearch.mapper.GithubRepositoryResponseMapper;
import com.example.reposearch.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}