package com.chaechae.realworldspringboot.article.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MultipleArticleResponse {
    List<ArticleResponse> articles;
    Long totalCount;

    @Builder
    public MultipleArticleResponse(List<ArticleResponse> articles, Long totalCount) {
        this.articles = articles;
        this.totalCount = totalCount;
    }
}
