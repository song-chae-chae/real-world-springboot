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
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ArticleServiceTest {
    @Autowired
    ArticleService articleService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    TagRepository tagRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @BeforeEach
    void clean() {
        favoriteRepository.deleteAll();
        commentRepository.deleteAll();
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void afterClean() {
        favoriteRepository.deleteAll();
        commentRepository.deleteAll();
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser(String name) {
        return User.builder()
                .name(name)
                .email("email")
                .image("image")
                .socialId("socialId")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("????????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();
        //when
        articleService.createArticle(savedUser.getId(), articleCreate);

        //then
        assertThat(articleRepository.count()).isEqualTo(1);
        assertThat(articleRepository.findAll().get(0).getTitle()).isEqualTo("??????");
        assertThat(tagRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("????????? ??????")
    @Transactional
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        Article savedArticle = articleRepository.findById(savedArticleId).get();

        //when
        List<String> updateTags = new ArrayList<>();
        updateTags.add("tag1");
        updateTags.add("tag2");

        ArticleUpdate articleUpdate = ArticleUpdate.builder().title("??????-update").description("??????- update").content("??????-update").tags(updateTags).build();
        articleService.updateArticle(savedUser.getId(), savedArticle.getId(), articleUpdate);
        Article updatedArticle = articleRepository.findById(savedArticleId).get();

        //then
        assertThat(articleRepository.findAll().get(0).getTitle()).isEqualTo("??????-update");
        assertThat(new ArrayList<>(updatedArticle.getTags()).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @Transactional
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);
        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();

        //when
        ArticleResponse articleResponse = articleService.get(userLoginResponse, savedArticleId);

        //then
        assertThat(articleResponse.getTitle()).isEqualTo("??????");
        assertThat(articleResponse.getContent()).isEqualTo("??????");
        assertThat(articleResponse.getTags().get(0)).isEqualTo("tag");
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void ?????????_??????_???????????????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<Article> articles = IntStream.range(1, 31)
                .mapToObj(i -> Article.builder()
                        .title("?????? " + i)
                        .content("?????? " + i)
                        .description("?????? " + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());
        articleRepository.saveAll(articles);
        ArticleSearch articleSearch = ArticleSearch.builder().author(savedUser.getId())
                .page(1)
                .size(10).build();
        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();
        //when
        MultipleArticleResponse list = articleService.getList(userLoginResponse, articleSearch);

        //then
        assertThat(list.getArticles().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        List<String> tags2 = new ArrayList<>();
        tags2.add("tag2");
        tags2.add("tag3");

        for (int i = 0; i < 5; i++) {
            ArticleCreate articleCreate = ArticleCreate.builder()
                    .title("?????? " + (i + 1))
                    .content("?????? " + (i + 1))
                    .description("?????? " + (i + 1))
                    .tags(tags)
                    .build();
            articleService.createArticle(savedUser.getId(), articleCreate);

            ArticleCreate articleCreate2 = ArticleCreate.builder()
                    .title("title " + (i + 1))
                    .content("content " + (i + 1))
                    .description("description " + (i + 1))
                    .tags(tags2)
                    .build();

            articleService.createArticle(savedUser2.getId(), articleCreate2);
        }

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .tag("tag2")
                .build();

        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();
        //when
        MultipleArticleResponse list = articleService.getList(userLoginResponse, articleSearch);

        //then
        assertThat(list.getArticles().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    public void ?????????_??????_?????????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        List<String> tags2 = new ArrayList<>();
        tags2.add("tag2");
        tags2.add("tag3");

        for (int i = 0; i < 5; i++) {
            ArticleCreate articleCreate = ArticleCreate.builder()
                    .title("?????? " + (i + 1))
                    .content("?????? " + (i + 1))
                    .description("?????? " + (i + 1))
                    .tags(tags)
                    .build();
            articleService.createArticle(savedUser.getId(), articleCreate);

            ArticleCreate articleCreate2 = ArticleCreate.builder()
                    .title("title " + (i + 1))
                    .content("content " + (i + 1))
                    .description("description " + (i + 1))
                    .tags(tags2)
                    .build();

            articleService.createArticle(savedUser2.getId(), articleCreate2);
        }

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .author(savedUser.getId())
                .build();

        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();
        //when
        MultipleArticleResponse list = articleService.getList(userLoginResponse, articleSearch);

        //then
        assertThat(list.getArticles().size()).isEqualTo(5);
        assertThat(list.getArticles().get(0).getAuthor().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ????????? ??????")
    public void ?????????_??????_?????????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        List<String> tags2 = new ArrayList<>();
        tags2.add("tag2");
        tags2.add("tag3");

        List<Long> savedArticleIdList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            ArticleCreate articleCreate = ArticleCreate.builder()
                    .title("?????? " + (i + 1))
                    .content("?????? " + (i + 1))
                    .description("?????? " + (i + 1))
                    .tags(tags)
                    .build();
            savedArticleIdList.add(articleService.createArticle(savedUser.getId(), articleCreate));

            ArticleCreate articleCreate2 = ArticleCreate.builder()
                    .title("title " + (i + 1))
                    .content("content " + (i + 1))
                    .description("description " + (i + 1))
                    .tags(tags2)
                    .build();

            savedArticleIdList.add(articleService.createArticle(savedUser2.getId(), articleCreate2));
        }
        articleService.favoriteCreate(savedUser.getId(), savedArticleIdList.get(0));
        articleService.favoriteCreate(savedUser.getId(), savedArticleIdList.get(1));

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .favorite(savedUser.getId())
                .build();
        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();

        //when
        MultipleArticleResponse list = articleService.getList(userLoginResponse, articleSearch);

        //then
        assertThat(list.getArticles().size()).isEqualTo(2);
        assertThat(list.getArticles().get(0).getAuthor().getId()).isEqualTo(savedUser2.getId());
        assertThat(list.getArticles().get(1).getAuthor().getId()).isEqualTo(savedUser.getId());
    }


    @Test
    @DisplayName("????????? ??????")
    public void ?????????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);
        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();
        //when
        articleService.deleteArticle(savedUser.getId(), savedArticleId);

        //then
        assertThrows(ArticleException.class, () -> {
            articleService.get(userLoginResponse, savedArticleId);
        });
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder()
                .content("??????")
                .build();
        //when
        Long commentId = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate);

        //then
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));
        assertThat(comment.getContent()).isEqualTo("??????");
    }

    @Test
    @DisplayName("?????? ??????")
    public void ??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder()
                .content("??????")
                .build();
        Long commentId = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate);

        //when
        articleService.deleteComment(savedUser.getId(), savedArticleId, commentId);

        //then
        CommentException commentException = assertThrows(CommentException.class, () -> commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND)));

        assertThat(commentException.getExceptionType().getMessage()).isEqualTo(CommentExceptionType.COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("?????? ??????")
    public void ??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder()
                .content("??????")
                .build();
        Long commentId = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate);

        //when
        CommentUpdate commentUpdate = CommentUpdate.builder().content("?????? ??????").build();

        articleService.updateComment(savedUser.getId(), commentId, commentUpdate);

        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));
        assertThat(findComment.getContent()).isEqualTo("?????? ??????");
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    public void ??????_??????_??????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();
        CommentCreate commentCreate2 = CommentCreate.builder().content("??????2").build();

        Long commentId = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate);
        Long commentId2 = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate2);

        UserLoginResponse userLoginResponse = UserLoginResponse.builder().id(savedUser.getId()).build();
        //when
        List<CommentResponse> commentList = articleService.getCommentList(userLoginResponse, savedArticleId);

        //then
        assertThat(commentList.size()).isEqualTo(2);
        assertThat(commentList.get(0).getContent()).isEqualTo("??????");
        assertThat(commentList.get(0).getAuthor().isFollowing()).isEqualTo(false);
    }


    @Test
    @DisplayName("????????? ?????????")
    public void ?????????_?????????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        //when
        Long favoriteId = articleService.favoriteCreate(savedUser.getId(), savedArticleId);

        //then
        Favorite savedFavorite = favoriteRepository.findById(favoriteId).orElseThrow(() -> new FavoriteException(FavoriteExceptionType.FAVORITE_NOT_FOUND));

        assertThat(savedFavorite.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(savedFavorite.getArticle().getId()).isEqualTo(savedArticleId);
    }

    @Test
    @DisplayName("????????? ??????")
    public void ?????????_????????????() throws Exception {
        //given
        User user = User.builder()
                .name("??????1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("??????")
                .description("??????")
                .content("??????")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);
        Long favoriteId = articleService.favoriteCreate(savedUser.getId(), savedArticleId);

        //when
        articleService.favoriteCancel(savedUser.getId(), savedArticleId);

        //then
        FavoriteException favoriteException = assertThrows(FavoriteException.class, () -> favoriteRepository.findByArticleIdAndUserId(savedArticleId, savedUser.getId())
                .orElseThrow(() -> new FavoriteException(FavoriteExceptionType.FAVORITE_NOT_FOUND)));

        assertThat(favoriteException.getExceptionType().getMessage()).isEqualTo(FavoriteExceptionType.FAVORITE_NOT_FOUND.getMessage());
    }
}