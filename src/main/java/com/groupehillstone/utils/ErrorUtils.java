package com.groupehillstone.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ErrorUtils {

    public static ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpStatus httpStatus,
            List<ErrorResponse.ValidationError> errors
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                message
        );
        errorResponse.setErrors(errors);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    public static ErrorResponse buildErrorResponseValidator(
            String message,
            HttpStatus httpStatus,
            List<ErrorResponse.ValidationError> errors
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                message
        );
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}
