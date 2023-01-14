package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;
import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public List<Article> getList(ArticleSearch articleSearch) {
        return jpaQueryFactory.selectFrom(article)
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Article> getArticleListByTag(ArticleSearch articleSearch) {
        return jpaQueryFactory.selectFrom(article)
                .join(article.tags, tag)
                .where(tag.tagName.eq(articleSearch.getTag()))
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Article> getArticleListByAuthor(ArticleSearch articleSearch) {
        return jpaQueryFactory.selectFrom(article)
                .join(article.user, user)
                .where(user.id.eq(articleSearch.getAuthor()))
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Article> getArticleListByUserFavorite(ArticleSearch articleSearch) {
        return jpaQueryFactory.selectFrom(article)
                .join(article.favorites, favorite)
                .where(favorite.user.id.eq(articleSearch.getFavorite()))
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Article> getFeed(Long authId, ArticleSearch articleSearch) {
        List<Follow> byFollowerId = followRepository.findByFollowerId(authId);
        List<Long> followedIdList = byFollowerId.stream().map(i -> i.getFollowed().getId()).collect(Collectors.toList());

        followedIdList.add(authId);

        return jpaQueryFactory.selectFrom(article)
                .where(user.id.in(followedIdList))
                .limit(articleSearch.getSize())
                .offset(articleSearch.getOffset())
                .orderBy(article.createdAt.desc())
                .fetch();
    }
}
