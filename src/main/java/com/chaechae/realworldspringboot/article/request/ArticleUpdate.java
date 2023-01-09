package com.chaechae.realworldspringboot.article.request;

import com.chaechae.realworldspringboot.article.domain.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@NoArgsConstructor
public class ArticleUpdate {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;
    @NotBlank(message = "설명을 입력하세요.")
    private String description;
    @NotBlank(message = "내용을 입력하세요.")
    private String content;
    private Set<Tag> tags;

    @Builder
    public ArticleUpdate(String title, String description, String content, Set<Tag> tags) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.tags = tags;
    }
}
