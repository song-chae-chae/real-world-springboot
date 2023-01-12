package com.chaechae.realworldspringboot.article.response.author;

import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Author {
    private final Long id;
    private final String name;
    private final String email;
    private final String image;
    private final String socialId;
    private final boolean following;

    @Builder
    public Author(Long id, String name, String email, String image, String socialId, boolean following) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.socialId = socialId;
        this.following = following;
    }

    public Author(ProfileResponse profileResponse) {
        this.id = profileResponse.getId();
        this.email = profileResponse.getEmail();
        this.name = profileResponse.getName();
        this.image = profileResponse.getImage();
        this.socialId = profileResponse.getSocialId();
        this.following = profileResponse.isFollowing();
    }
}
