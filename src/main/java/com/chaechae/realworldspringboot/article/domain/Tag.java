package com.chaechae.realworldspringboot.article.domain;

import com.chaechae.realworldspringboot.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Tag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void setArticle(Article article) {
        if (this.article != null) {
            this.article.getTags().remove(this);
        }
        this.article = article;
        this.article.getTags().add(this);
    }
}
