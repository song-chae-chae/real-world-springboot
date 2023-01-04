package com.chaechae.realworldspringboot.user.response;

import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {
    private final String email;
    private final String username;
    private final String image;
    @Builder
    public UserResponse(User user) {
        this.email = user.getEmail();
        this.username = user.getName();
        this.image = user.getImage();
    }
}
