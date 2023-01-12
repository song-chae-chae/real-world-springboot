package com.chaechae.realworldspringboot.article.domain;

import com.chaechae.realworldspringboot.article.request.CommentUpdate;
import com.chaechae.realworldspringboot.base.BaseEntity;
import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public Comment(Long id, String content, Article article, User user) {
        this.id = id;
        this.article = article;
        this.content = content;
        this.user = user;
    }

    public void setArticle(Article article) {
        if (this.article != null) {
            this.article.getComments().remove(this);
        }
        this.article = article;
        this.article.getComments().add(this);
    }

    public void update(CommentUpdate commentUpdate) {
        this.content = commentUpdate.getContent();
    }
}
