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
import com.chaechae.realworldspringboot.article.response.author.Author;
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
    public ArticleResponse get(Long authId, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));
        return convertArticleResponse(authId, article);
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
        List<Article> articleList;
        if (articleSearch.getAuthor() != null) {
            articleList = articleRepository.getArticleListByAuthor(articleSearch);
        } else if (articleSearch.getFavorite() != null) {
            articleList = articleRepository.getArticleListByUserFavorite(articleSearch);
        } else if (articleSearch.getTag() != null) {
            articleList = articleRepository.getArticleListByTag(articleSearch);
        } else {
            articleList = articleRepository.getList(articleSearch);
        }

        return articleList.stream().map(i -> convertArticleResponse(authId, i)).collect(Collectors.toList());
    }

    private ArticleResponse convertArticleResponse(Long authId, Article article) {
        ProfileResponse profileResponse = profileService.get(authId, article.getUser().getId());
        Long favoritesCount = favoriteRepository.countByArticleId(article.getId());

        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .content(article.getContent())
                .description(article.getDescription())
                .tags(article.getTags())
                .isFavorite(isFavorite(authId, article.getId()))
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
    private boolean isFavorite(Long authId, Long articleId) {
        if (authId == null) {
            return false;
        }

        return favoriteRepository.findByArticleIdAndUserId(articleId, authId).isPresent();
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

    public List<CommentResponse> getCommentList(Long authId, Long articleId) {
        List<Comment> comments = commentRepository.findByArticleId(articleId);

        return comments.stream().map(comment -> convertCommentResponse(authId, comment)).collect(Collectors.toList());
    }

    private CommentResponse convertCommentResponse(Long authId, Comment comment) {
        ProfileResponse profileResponse = profileService.get(authId, comment.getUser().getId());
        return CommentResponse.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .content(comment.getContent())
                .author(new Author(profileResponse))
                .build();
    }

    public List<ArticleResponse> getFeed(Long authId, ArticleSearch articleSearch) {
        List<Article> feed = articleRepository.getFeed(authId, articleSearch);
        System.out.println("feed count " + feed.size());
        return feed.stream().map(article -> convertArticleResponse(authId, article)).collect(Collectors.toList());
    }
}
