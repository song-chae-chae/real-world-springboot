package com.chaechae.realworldspringboot.article.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleUpdate {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;
    @NotBlank(message = "설명을 입력하세요.")
    private String description;
    @NotBlank(message = "내용을 입력하세요.")
    private String content;
    private List<String> tags;

    @Builder
    public ArticleUpdate(String title, String description, String content, List<String> tags) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.tags = tags;
    }
}
