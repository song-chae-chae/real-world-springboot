package com.chaechae.realworldspringboot.article.response;

import com.chaechae.realworldspringboot.article.domain.Tag;
import com.chaechae.realworldspringboot.article.response.author.Author;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String content;
    private String description;
    private List<String> tags;
    private Author author;

    @Builder
    public ArticleResponse(Long id, String title, LocalDateTime createdAt, LocalDateTime modifiedAt, String content, String description, Set<Tag> tags, Author author) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.content = content;
        this.description = description;
        this.tags = tags.stream().map(Tag::getTagName).collect(Collectors.toList());
        this.author = author;
    }
 }
