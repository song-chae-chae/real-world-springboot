package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.request.*;
import com.chaechae.realworldspringboot.article.response.ArticleResponse;
import com.chaechae.realworldspringboot.article.response.CommentResponse;
import com.chaechae.realworldspringboot.article.response.MultipleArticleResponse;
import com.chaechae.realworldspringboot.article.service.ArticleService;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/articles")
    public ResponseEntity<Long> createArticle(@AuthenticationPrincipal UserLoginResponse authUser, @Valid @RequestBody ArticleCreate request) {
        Long articleId = articleService.createArticle(authUser.getId(), request);
        return ResponseEntity.status(200).body(articleId);
    }

    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> getArticle(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId) {
        ArticleResponse articleResponse = articleService.get(authUser, articleId);
        return ResponseEntity.status(200).body(articleResponse);
    }

    @PutMapping("/articles/{articleId}")
    public void updateArticle(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId, @Valid @RequestBody ArticleUpdate request) {
        articleService.updateArticle(authUser.getId(), articleId, request);
    }

    @DeleteMapping("/articles/{articleId}")
    public void deleteArticle(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId) {
        articleService.deleteArticle(authUser.getId(), articleId);
    }

    @GetMapping("/articles")
    public ResponseEntity<MultipleArticleResponse> getAllArticles(@AuthenticationPrincipal UserLoginResponse authUser, @ModelAttribute ArticleSearch articleSearch) {
        MultipleArticleResponse list = articleService.getList(authUser, articleSearch);

        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/articles/feed")
    public ResponseEntity<MultipleArticleResponse> getFeed(@AuthenticationPrincipal UserLoginResponse authUser, @ModelAttribute ArticleSearch articleSearch) {
        MultipleArticleResponse feed = articleService.getFeed(authUser, articleSearch);

        return ResponseEntity.status(200).body(feed);
    }

    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId) {
        List<CommentResponse> list = articleService.getCommentList(authUser, articleId);
        return ResponseEntity.status(200).body(list);
    }

    @PostMapping("/articles/{articleId}/comment")
    public void createComment(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId, @Valid @RequestBody CommentCreate commentCreate) {
        articleService.createComment(authUser.getId(), articleId, commentCreate);
    }

    @DeleteMapping("/articles/{articleId}/comment/{commentId}")
    public void deleteComment(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId, @PathVariable Long commentId) {
        articleService.deleteComment(authUser.getId(), articleId, commentId);
    }

    @PatchMapping("/articles/{articleId}/comment/{commentId}")
    public void updateComment(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long commentId, @Valid @RequestBody CommentUpdate commentUpdate) {
        articleService.updateComment(authUser.getId(), commentId, commentUpdate);
    }

    @PostMapping("/articles/{articleId}/favorite")
    public void favoriteCreate(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId) {
        articleService.favoriteCreate(authUser.getId(), articleId);
    }

    @DeleteMapping("/articles/{articleId}/favorite")
    public void favoriteCancel(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId) {
        articleService.favoriteCancel(authUser.getId(), articleId);
    }
}
