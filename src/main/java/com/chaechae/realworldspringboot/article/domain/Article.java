package com.chaechae.realworldspringboot.article.domain;

import com.chaechae.realworldspringboot.article.request.ArticleUpdate;
import com.chaechae.realworldspringboot.base.BaseEntity;
import com.chaechae.realworldspringboot.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Article extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String description;

    @OneToMany(mappedBy = "article")
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "article")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article")
    private List<Favorite> favorites = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Article(Long id, String title, String content, String description, Set<Tag> tags, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.description = description;
        this.user = user;
    }

    public void update(ArticleUpdate request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.description = request.getDescription();
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
