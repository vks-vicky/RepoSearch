package com.example.reposearch.model;

import java.util.Arrays;

public enum SortDirection {

    ASC("asc"),
    DESC("desc");

    private final String requestValue;

    SortDirection(String requestValue) {
        this.requestValue = requestValue;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public static SortDirection fromRequestValue(String value) {
        return Arrays.stream(values())
                .filter(direction ->
                        direction.requestValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported sort direction: " + value
                        ));
    }
}