package com.chaechae.realworldspringboot.article.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor
public class Tag {
    private Long id;
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Builder
    public Tag(Long id, String tagName, Article article) {
        this.id = id;
        this.tagName = tagName;
        this.article = article;
    }
}
