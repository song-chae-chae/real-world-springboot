package com.chaechae.realworldspringboot.article.service;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Comment;
import com.chaechae.realworldspringboot.article.domain.Favorite;
import com.chaechae.realworldspringboot.article.domain.Tag;
import com.chaechae.realworldspringboot.article.exception.*;
import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.CommentRepository;
import com.chaechae.realworldspringboot.article.repository.FavoriteRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.*;
import com.chaechae.realworldspringboot.article.response.ArticleResponse;
import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import com.chaechae.realworldspringboot.profile.service.ProfileService;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final TagRepository tagRepository;
    private final ProfileService profileService;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;

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

    @Transactional(readOnly = true)
    public List<ArticleResponse> getList(Long authId, ArticleSearch articleSearch) {
        List<Article> list = articleRepository.getList(articleSearch);

        return list.stream().map(i -> convertArticleResponse(authId, i)).collect(Collectors.toList());
    }

    private ArticleResponse convertArticleResponse(Long authId, Article article) {
        ProfileResponse profileResponse = profileService.get(authId, article.getUser().getId());

        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .content(article.getContent())
                .description(article.getDescription())
                .tags(article.getTags())
                .author(ArticleResponse.Author.builder()
                        .id(profileResponse.getId())
                        .name(profileResponse.getName())
                        .image(profileResponse.getImage())
                        .email(profileResponse.getEmail())
                        .socialId(profileResponse.getSocialId())
                        .following(profileResponse.isFollowing())
                        .build())
                .build();
    }

    public Long createComment(Long authId, Long articleId, CommentCreate commentCreate) {
        Article savedArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));
        User authUser = userService.get(authId);
        Comment comment = Comment.builder()
                .article(savedArticle)
                .content(commentCreate.getContent())
                .user(authUser)
                .build();

        return commentRepository.save(comment).getId();
    }

    public void deleteComment(Long authId, Long articleId, Long commentId) {
        Comment savedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!(savedComment.getUser().getId().equals(authId))) {
            throw new CommentException(CommentExceptionType.COMMENT_UNAUTHORIZED);
        }

        commentRepository.delete(savedComment);
    }

    @Transactional
    public void updateComment(Long authId, Long commentId, CommentUpdate commentUpdate) {
        Comment savedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!(savedComment.getUser().getId().equals(authId))) {
            throw new CommentException(CommentExceptionType.COMMENT_UNAUTHORIZED);
        }

        savedComment.update(commentUpdate);
    }

    @Transactional
    public Long favoriteCreate(Long authId, Long articleId) {
        Article savedArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));
        User savedUser = userService.get(authId);
        Favorite favoriteCreate = Favorite.builder()
                .article(savedArticle)
                .user(savedUser)
                .build();

        return favoriteRepository.save(favoriteCreate).getId();
    }

    @Transactional
    public void favoriteCancel(Long authId, Long articleId) {
        Favorite savedFavorite = favoriteRepository.findByArticleIdAndUserId(articleId, authId)
                .orElseThrow(() -> new FavoriteException(FavoriteExceptionType.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(savedFavorite);
    }
}
