package com.chaechae.realworldspringboot.base.exception;

import org.springframework.http.HttpStatus;

public interface RealWorldExceptionType {
    HttpStatus getStatus();
    String getMessage();
}
