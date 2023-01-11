package com.chaechae.realworldspringboot.article.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.chaechae.realworldspringboot.article.domain.QTag.tag;

@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> getTagNameList() {
        return jpaQueryFactory
                .selectFrom(tag)
                .select(tag.tagName)
                .distinct()
                .fetch();
    }
}
