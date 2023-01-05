package com.chaechae.realworldspringboot.profile.exception;

import com.chaechae.realworldspringboot.base.exception.RealWorldExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowExceptionType implements RealWorldExceptionType {
    FOLLOWING_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 되어있지 않습니다."),
    ALREADY_FOLLOWED(HttpStatus.BAD_REQUEST, "이미 팔로우 되어있습니다.");

    private final HttpStatus status;
    private final String message;
}
