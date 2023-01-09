package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.request.ArticleCreate;
import com.chaechae.realworldspringboot.article.request.ArticleUpdate;
import com.chaechae.realworldspringboot.article.service.ArticleService;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/articles")
    public void createArticle(@AuthenticationPrincipal UserLoginResponse authUser, @Valid @RequestBody ArticleCreate request) {
        articleService.createArticle(authUser.getId(),request);
    }
    @PutMapping("/articles/{articleId}")
    public void updateArticle(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId, @Valid @RequestBody ArticleUpdate request) {
        articleService.updateArticle(authUser.getId(), articleId, request);
    }

    @DeleteMapping("/articles/{articleId}")
    public void deleteArticle(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable Long articleId) {
        articleService.deleteArticle(authUser.getId(), articleId);
    }
}
