package com.chaechae.realworldspringboot.base.exception.response;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * {
 * "code": "404",
 * "message": "error message",
 * "validation": {
 * "error": "invalid_grant",
 * "error_code": "KOE320",
 * "error_description": "authorization code not found ~"
 * }
 * }
 */
@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation != null ? validation : new HashMap<>();
    }
}
