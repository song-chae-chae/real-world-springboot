package com.chaechae.realworldspringboot.user.response;

import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginResponse {
    private Long id;
    private String name;
    private String email;
    private String image;
    private String token;
    private String refreshToken;

    @Builder
    public UserLoginResponse(Long id, String name, String email, String image, String token, String refreshToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    @Builder
    public UserLoginResponse(User user, Token token) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.image = user.getImage();
        this.token = token.getToken();
        this.refreshToken = token.getRefreshToken();
    }
}
