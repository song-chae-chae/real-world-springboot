package com.chaechae.realworldspringboot.article.service;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Tag;
import com.chaechae.realworldspringboot.article.exception.ArticleException;
import com.chaechae.realworldspringboot.article.exception.ArticleExceptionType;
import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.ArticleCreate;
import com.chaechae.realworldspringboot.article.request.ArticleUpdate;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final TagRepository tagRepository;

    public Long createArticle(Long id, ArticleCreate request) {
        User savedUser = userService.get(id);

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .description(request.getDescription())
                .user(savedUser)
                .build();

        Article save = articleRepository.save(article);

        if (request.getTags() != null) {
            for (int i = 0; i < request.getTags().size(); i++) {
                Tag tag = convertTag(request.getTags().get(i));
                tag.setArticle(article);
                tagRepository.save(tag);
            }
        }

        return save.getId();
    }

    private Tag convertTag(String tagName) {
        return Tag.builder().tagName(tagName).build();
    }

    @Transactional
    public void updateArticle(Long authId, Long articleId, ArticleUpdate request) {
        Article savedArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));

        if (!savedArticle.getUser().getId().equals(authId)) {
            throw new ArticleException(ArticleExceptionType.ARTICLE_UNAUTHORIZED);
        }

        savedArticle.update(request);
    }

    @Transactional(readOnly = true)
    public Article get(Long id) {
        return articleRepository.findById(id).orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));
    }

    @Transactional
    public void deleteArticle(Long authId, Long articleId) {
        Article savedArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));

        if (savedArticle.getUser().getId().equals(authId)) {
            tagRepository.deleteByArticleId(articleId);
            articleRepository.deleteById(articleId);
        }
    }
}
