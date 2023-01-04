package com.chaechae.realworldspringboot.security.jwt.controller;

import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private final TokenService tokenService;

    @DeleteMapping("/token/expired")
    public void expireToken(@RequestHeader("Refresh") String refreshToken) {
        // refresh token 받아서 삭제
        tokenService.deleteRefreshToken(refreshToken);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<String> refreshAuth(@RequestHeader("Refresh") String refreshToken) {
        Token token = tokenService.updateRefreshToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token.getToken());
        headers.add("Refresh", token.getRefreshToken());

        return ResponseEntity.status(200).headers(headers).body("refresh token");
    }
}
