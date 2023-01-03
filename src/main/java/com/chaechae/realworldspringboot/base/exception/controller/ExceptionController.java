package com.chaechae.realworldspringboot.base.exception.controller;

import com.chaechae.realworldspringboot.base.exception.RestTemplateException;
import com.chaechae.realworldspringboot.base.exception.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, Object> validation = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("잘못된 요청입니다.")
                .code("400")
                .validation(validation)
                .build();

        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(RestTemplateException.class)
    public ResponseEntity<ErrorResponse> restTemplateExceptionHandler(RestTemplateException e) {
        int statusCode = e.getExceptionType().getStatus().value();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getExceptionType().getMessage())
                .validation(e.getValidation()).build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
