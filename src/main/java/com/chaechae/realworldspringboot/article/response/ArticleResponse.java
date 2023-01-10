package com.chaechae.realworldspringboot.article.response;

import com.chaechae.realworldspringboot.article.domain.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String content;
    private String description;
    private Set<Tag> tags;
    private Author author;

    @Builder
    public ArticleResponse(Long id, String title, LocalDateTime createdAt, LocalDateTime modifiedAt, String content, String description, Set<Tag> tags, Author author) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.content = content;
        this.description = description;
        this.tags = tags;
        this.author = author;
    }

    @Getter
    public static class Author {
        private final Long id;
        private final String name;
        private final String email;
        private final String image;
        private final String socialId;
        private final boolean following;

        @Builder
        public Author(Long id, String name, String email, String image, String socialId, boolean following) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.image = image;
            this.socialId = socialId;
            this.following = following;
        }
    }
}
