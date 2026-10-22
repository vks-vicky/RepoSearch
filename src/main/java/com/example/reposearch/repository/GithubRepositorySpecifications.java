package com.example.reposearch.repository;

import com.example.reposearch.entity.GithubRepositoryEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Locale;

public final class GithubRepositorySpecifications {

    private GithubRepositorySpecifications() {
    }

    public static Specification<GithubRepositoryEntity> withFilters(
            String language,
            Long minStars
    ) {
        Specification<GithubRepositoryEntity> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (StringUtils.hasText(language)) {
            String normalizedLanguage =
                    language.trim().toLowerCase(Locale.ROOT);

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(root.get("language")),
                                    normalizedLanguage
                            )
            );
        }

        if (minStars != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("starsCount"),
                                    minStars
                            )
            );
        }

        return specification;
    }
}