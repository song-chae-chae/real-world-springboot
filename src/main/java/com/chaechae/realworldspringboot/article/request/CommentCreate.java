package com.chaechae.realworldspringboot.article.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class CommentCreate {
    @NotBlank(message = "댓글을 입력해주세요.")
    private String content;

    @Builder
    public CommentCreate(String content) {
        this.content = content;
    }
}
