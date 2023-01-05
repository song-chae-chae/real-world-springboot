package com.chaechae.realworldspringboot.article.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String description;

    @OneToMany(mappedBy = "article")
    private Set<Tag> tags;

    @Builder
    public Article(Long id, String title, String content, String description, Set<Tag> tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.description = description;
        this.tags = tags;
    }
}
