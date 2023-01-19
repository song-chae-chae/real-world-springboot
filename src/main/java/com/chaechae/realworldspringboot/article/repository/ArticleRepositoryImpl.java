package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;
import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.chaechae.realworldspringboot.article.domain.QArticle.article;
import static com.chaechae.realworldspringboot.article.domain.QFavorite.favorite;
import static com.chaechae.realworldspringboot.article.domain.QTag.tag;
import static com.chaechae.realworldspringboot.user.domain.QUser.user;

@Slf4j
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final FollowRepository followRepository;

    @Override
    public Page<Article> getList(ArticleSearch articleSearch) {
        List<Article> articles = jpaQueryFactory.selectFrom(article)
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(article.count())
                .from(article);

        return getPageArticle(totalCount, articles, articleSearch);

    }

    @Override
    public Page<Article> getArticleListByTag(ArticleSearch articleSearch) {
        List<Article> articles = jpaQueryFactory.selectFrom(article)
                .join(article.tags, tag)
                .where(tag.tagName.eq(articleSearch.getTag()))
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(article.count())
                .from(article)
                .join(article.tags, tag)
                .where(tag.tagName.eq(articleSearch.getTag()));

        return getPageArticle(totalCount, articles, articleSearch);
    }

    @Override
    public Page<Article> getArticleListByAuthor(ArticleSearch articleSearch) {
        List<Article> articles = jpaQueryFactory.selectFrom(article)
                        .join(article.user, user)
                        .where(user.id.eq(articleSearch.getAuthor()))
                        .limit(articleSearch.getSize())
                        .offset(articleSearch.getOffset())
                        .orderBy(article.createdAt.desc())
                        .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(article.count())
                .from(article)
                .join(article.user, user)
                .where(user.id.eq(articleSearch.getAuthor()));

        return getPageArticle(totalCount, articles, articleSearch);
    }

    @Override
    public Page<Article> getArticleListByUserFavorite(ArticleSearch articleSearch) {
        List<Article> articles =jpaQueryFactory.selectFrom(article)
                        .join(article.favorites, favorite)
                        .where(favorite.user.id.eq(articleSearch.getFavorite()))
                        .limit(articleSearch.getSize())
                        .offset(articleSearch.getOffset())
                        .orderBy(article.createdAt.desc())
                        .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(article.count())
                .from(article)
                .join(article.favorites, favorite)
                .where(favorite.user.id.eq(articleSearch.getFavorite()));

        return getPageArticle(totalCount, articles, articleSearch);
    }

    @Override
    public Page<Article> getFeed(Long authId, ArticleSearch articleSearch) {
        List<Follow> byFollowerId = followRepository.findByFollowerId(authId);
        List<Long> followedIdList = byFollowerId.stream().map(i -> i.getFollowed().getId()).collect(Collectors.toList());

        followedIdList.add(authId);
        List<Article> articles =jpaQueryFactory.selectFrom(article)
                        .where(user.id.in(followedIdList))
                        .limit(articleSearch.getSize())
                        .offset(articleSearch.getOffset())
                        .orderBy(article.createdAt.desc())
                        .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(article.count())
                .from(article)
                .where(user.id.in(followedIdList));

        return getPageArticle(totalCount, articles, articleSearch);
    }

    private Page<Article> getPageArticle(JPAQuery<Long> jpaQuery, List<Article> articles, ArticleSearch articleSearch) {
        PageRequest of = PageRequest.of(articleSearch.getPage() - 1, articleSearch.getSize());

        return PageableExecutionUtils.getPage(articles, of, jpaQuery::fetchOne);
    }
}
