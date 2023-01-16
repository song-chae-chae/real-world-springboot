package com.chaechae.realworldspringboot.article.response.author;

import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Author {
    private final Long id;
    private final String name;
    private final String email;
    private final String image;
    private final String socialId;
    private final LocalDateTime createdAt;
    private final boolean following;

    @Builder
    public Author(Long id, String name, String email, String image, String socialId, LocalDateTime createdAt, boolean following) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.socialId = socialId;
        this.createdAt = createdAt;
        this.following = following;
    }

    public Author(ProfileResponse profileResponse) {
        this.id = profileResponse.getId();
        this.email = profileResponse.getEmail();
        this.name = profileResponse.getName();
        this.image = profileResponse.getImage();
        this.socialId = profileResponse.getSocialId();
        this.createdAt = profileResponse.getCreatedAt();
        this.following = profileResponse.isFollowing();
    }
}
