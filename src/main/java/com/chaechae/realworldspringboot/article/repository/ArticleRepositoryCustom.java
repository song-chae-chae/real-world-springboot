package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> getList(ArticleSearch articleSearch);
}
