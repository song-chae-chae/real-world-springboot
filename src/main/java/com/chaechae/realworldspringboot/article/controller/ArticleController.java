package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
}
