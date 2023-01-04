package com.chaechae.realworldspringboot.security.jwt.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Token {
    private String token;
    private String refreshToken;

    @Builder
    public Token(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
