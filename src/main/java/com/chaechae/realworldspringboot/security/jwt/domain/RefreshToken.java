package com.chaechae.realworldspringboot.security.jwt.domain;

import com.chaechae.realworldspringboot.base.BaseEntity;
import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RefreshToken(Long id, String refreshToken, User user) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public void update(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
