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
import com.chaechae.realworldspringboot.article.response.CommentResponse;
import com.chaechae.realworldspringboot.article.response.MultipleArticleResponse;
import com.chaechae.realworldspringboot.article.response.author.Author;
import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import com.chaechae.realworldspringboot.profile.service.ProfileService;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import com.chaechae.realworldspringboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public Long createArticle(Long authId, ArticleCreate request) {
        User savedUser = userService.get(authId);

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .description(request.getDescription())
                .user(savedUser)
                .build();

        Article save = articleRepository.save(article);

        if (request.getTags() != null) {
            saveTag(save, request.getTags());
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

        if (request.getTags() != null) {
            tagRepository.deleteByArticleId(articleId);
            saveTag(savedArticle, request.getTags());
        }

        savedArticle.update(request);
    }

    private void saveTag(Article article, List<String> tagList) {
        for (String tagName : tagList) {
            Tag tag = convertTag(tagName);
            tag.setArticle(article);
            tagRepository.save(tag);
        }
    }
    @Transactional(readOnly = true)
    public ArticleResponse get(UserLoginResponse authUser, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));
        return convertArticleResponse(authUser, article);
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
    public MultipleArticleResponse getList(UserLoginResponse authUser, ArticleSearch articleSearch) {
        List<Article> articleList;
        Page<Article> articlePage;

        if (articleSearch.getAuthor() != null) {
            articlePage = articleRepository.getArticleListByAuthor(articleSearch);
        } else if (articleSearch.getFavorite() != null) {
            articlePage = articleRepository.getArticleListByUserFavorite(articleSearch);
        } else if (articleSearch.getTag() != null) {
            articlePage = articleRepository.getArticleListByTag(articleSearch);
        } else {
            articlePage = articleRepository.getList(articleSearch);
        }

        articleList = articlePage.getContent();

        List<ArticleResponse> articleResponses = articleList.stream().map(i -> convertArticleResponse(authUser, i)).collect(Collectors.toList());
        return MultipleArticleResponse.builder()
                .articles(articleResponses)
                .totalCount(articlePage.getTotalElements())
                .build();
    }

    private ArticleResponse convertArticleResponse(UserLoginResponse authUser, Article article) {
        ProfileResponse profileResponse = profileService.get(authUser, article.getUser().getId());
        Long favoritesCount = favoriteRepository.countByArticleId(article.getId());

        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .content(article.getContent())
                .description(article.getDescription())
                .tags(article.getTags())
                .isFavorite(isFavorite(authUser, article.getId()))
                .favoritesCount(favoritesCount)
                .author(Author.builder()
                        .id(profileResponse.getId())
                        .name(profileResponse.getName())
                        .image(profileResponse.getImage())
                        .email(profileResponse.getEmail())
                        .socialId(profileResponse.getSocialId())
                        .createdAt(profileResponse.getCreatedAt())
                        .following(profileResponse.isFollowing())
                        .build())
                .build();
    }

    private boolean isFavorite(UserLoginResponse authUser, Long articleId) {
        if (authUser == null) {
            return false;
        }

        return favoriteRepository.findByArticleIdAndUserId(articleId, authUser.getId()).isPresent();
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

    public List<CommentResponse> getCommentList(UserLoginResponse authUser, Long articleId) {
        List<Comment> comments = commentRepository.findByArticleId(articleId);

        return comments.stream().map(comment -> convertCommentResponse(authUser, comment)).collect(Collectors.toList());
    }

    private CommentResponse convertCommentResponse(UserLoginResponse authUser, Comment comment) {
        ProfileResponse profileResponse = profileService.get(authUser, comment.getUser().getId());
        return CommentResponse.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .content(comment.getContent())
                .author(new Author(profileResponse))
                .build();
    }

    public MultipleArticleResponse getFeed(UserLoginResponse authUser, ArticleSearch articleSearch) {
        if (authUser != null) {
            Page<Article> feed = articleRepository.getFeed(authUser.getId(), articleSearch);

            List<ArticleResponse> articleResponseList = feed.getContent()
                    .stream()
                    .map(article -> convertArticleResponse(authUser, article))
                    .collect(Collectors.toList());

            return MultipleArticleResponse.builder()
                    .articles(articleResponseList)
                    .totalCount(feed.getTotalElements()).build();
        }
        return null;
    }
}
