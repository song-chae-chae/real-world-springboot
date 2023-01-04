package com.chaechae.realworldspringboot.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialUserResponse {
    private String id;
    private String nickname;
    private String email;
    private String image;

    @Builder
    public SocialUserResponse(String id, String nickname, String email, String image) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.image = image;
    }
}
