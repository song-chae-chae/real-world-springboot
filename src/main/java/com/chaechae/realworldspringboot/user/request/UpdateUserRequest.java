package com.chaechae.realworldspringboot.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {
    @NotNull(message = "id를 입력해주세요.")
    private Long id;

    @NotBlank(message = "email을 입력해주세요.")
    @Email(message = "올바른 email을 입력해주세요.")
    private String email;

    @NotBlank(message = "image를 입력해주세요.")
    private String image;

    @Builder
    public UpdateUserRequest(Long id, String email, String image) {
        this.id = id;
        this.email = email;
        this.image = image;
    }
}
