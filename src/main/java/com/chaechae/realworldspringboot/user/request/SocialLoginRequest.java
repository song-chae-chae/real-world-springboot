package com.chaechae.realworldspringboot.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialLoginRequest {
    private String code;

    @Builder
    public SocialLoginRequest(String code) {
        this.code = code;
    }
}
