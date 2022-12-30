package com.chaechae.realworldspringboot.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class RestTemplateException extends RealWorldException {
    public RestTemplateException(RealWorldExceptionType restTemplateExceptionType, Map<String, String> validation) {
        super(restTemplateExceptionType);
        this.validation = validation != null ? validation : new HashMap<>();
    }

}
