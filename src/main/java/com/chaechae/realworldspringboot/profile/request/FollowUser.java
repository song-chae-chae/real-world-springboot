package com.chaechae.realworldspringboot.profile.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowUser {
    private Long id;

    @Builder
    public FollowUser(Long id) {
        this.id = id;
    }
}
