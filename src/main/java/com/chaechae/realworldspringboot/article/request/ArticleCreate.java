package com.chaechae.realworldspringboot.article.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleCreate {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    @NotBlank(message = "description을 입력해주세요.")
    private String description;
    private List<String> tags;

    @Builder
    public ArticleCreate(String title, String content, String description, List<String> tags) {
        this.title = title;
        this.content = content;
        this.description = description;
        this.tags = tags;
    }
}
