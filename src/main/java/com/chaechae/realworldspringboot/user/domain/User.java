package com.chaechae.realworldspringboot.user.domain;

import com.chaechae.realworldspringboot.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialId;
    private String name;
    private String email;

    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public User(String socialId, String name, String email, String image, Role role) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.image = image;
        this.role = role;
    }
}
