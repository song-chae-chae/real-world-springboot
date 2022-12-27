package com.chaechae.realworldspringboot.user.domain;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String name;
    private String email;
    private String password;

    private String bio;

    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @Builder
    public User(String userId, String name, String email, String password, String bio, String image, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.image = image;
        this.createdAt = LocalDateTime.now();
        this.role = role;
    }
}
