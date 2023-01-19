package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;
import org.springframework.data.domain.Page;

public interface ArticleRepositoryCustom {
    Page<Article> getList(ArticleSearch articleSearch);

    Page<Article> getArticleListByTag(ArticleSearch articleSearch);

    Page<Article> getArticleListByAuthor(ArticleSearch articleSearch);

    Page<Article> getArticleListByUserFavorite(ArticleSearch articleSearch);

    Page<Article> getFeed(Long authId, ArticleSearch articleSearch);
}
