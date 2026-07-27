package com.artgr.learner.data.presentation;

public record ErrorResponse(ErrorDetail error) {

    public record ErrorDetail(Integer status, String code, String message, String traceId) {
    }
}
