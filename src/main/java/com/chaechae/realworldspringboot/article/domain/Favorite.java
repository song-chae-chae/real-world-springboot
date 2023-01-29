package com.chaechae.realworldspringboot.article.domain;

import com.chaechae.realworldspringboot.base.BaseEntity;
import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Favorite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Favorite(Long id, Article article, User user) {
        this.id = id;
        this.article = article;
        this.user = user;
    }

    public void setArticle(Article article) {
        if (this.article != null) {
            this.article.getFavorites().remove(this);
        }
        this.article = article;
        this.article.getFavorites().add(this);
    }
}
