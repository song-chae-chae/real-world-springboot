package com.chaechae.realworldspringboot.article.response;

import com.chaechae.realworldspringboot.article.response.author.Author;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String content;
    private Author author;

    @Builder
    public CommentResponse(Long id, LocalDateTime createdAt, LocalDateTime modifiedAt, String content, Author author) {
        this.id = id;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.content = content;
        this.author = author;
    }
}
