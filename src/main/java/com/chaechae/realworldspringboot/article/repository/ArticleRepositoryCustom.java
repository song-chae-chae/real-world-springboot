package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> getList(ArticleSearch articleSearch);

    List<Article> getArticleListByTag(ArticleSearch articleSearch);

    List<Article> getArticleListByAuthor(ArticleSearch articleSearch);

    List<Article> getArticleListByUserFavorite(ArticleSearch articleSearch);

    List<Article> getFeed(Long authId, ArticleSearch articleSearch);
}
