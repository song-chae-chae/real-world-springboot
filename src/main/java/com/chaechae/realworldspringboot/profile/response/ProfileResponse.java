package com.chaechae.realworldspringboot.profile.response;

import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProfileResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String image;
    private final String socialId;
    private final LocalDateTime createdAt;
    private final boolean following;

    @Builder
    public ProfileResponse(User user, boolean following) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.image = user.getImage();
        this.socialId = user.getSocialId();
        this.createdAt = user.getCreatedAt();
        this.following = following;
    }
}
