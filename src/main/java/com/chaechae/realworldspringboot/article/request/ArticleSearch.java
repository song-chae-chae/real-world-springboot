package com.chaechae.realworldspringboot.article.request;

import lombok.Builder;
import lombok.Getter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Builder
public class ArticleSearch {
    private static final int MAX_SIZE = 2000;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 10;

    private String tag;
    private Long favorite;
    private Long author;

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}
