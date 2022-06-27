package com.groupehillstone.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {

    private final int status;

    private final String message;

    private List<ValidationError> errors;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ValidationError {

        private final String field;

        private final String message;

    }

}
