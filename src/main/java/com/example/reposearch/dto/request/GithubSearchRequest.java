package com.example.reposearch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GithubSearchRequest(

        @NotBlank(message = "query must not be blank")
        @Size(max = 256, message = "query must not exceed 256 characters")
        String query,

        @Size(max = 100, message = "language must not exceed 100 characters")
        String language,

        String sort,

        String direction
) {
}