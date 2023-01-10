package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.chaechae.realworldspringboot.article.domain.QArticle.article;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Article> getList(ArticleSearch articleSearch) {
        return jpaQueryFactory.selectFrom(article)
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();
    }
}
