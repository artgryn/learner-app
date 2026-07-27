package com.artgr.learner.exceptions;

import org.springframework.http.HttpStatus;

// Base for exceptions that should surface as the documented error body
// (doc/api/swagger.yaml ErrorResponse), not a generic 500.
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    protected ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
