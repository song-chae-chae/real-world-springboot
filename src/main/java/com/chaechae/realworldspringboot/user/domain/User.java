package com.chaechae.realworldspringboot.user.domain;

import com.chaechae.realworldspringboot.base.BaseEntity;
import com.chaechae.realworldspringboot.user.request.UpdateUserRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String socialId;
    private String name;
    private String email;

    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public User(Long id, String socialId, String name, String email, String image, Role role) {
        this.id = id;
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.image = image;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", socialId='" + socialId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", role=" + role +
                '}';
    }

    public void update(UpdateUserRequest request) {
        this.email = request.getEmail();
        this.image = request.getImage();
    }
}
