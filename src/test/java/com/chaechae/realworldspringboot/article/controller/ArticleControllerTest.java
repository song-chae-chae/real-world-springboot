package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Comment;
import com.chaechae.realworldspringboot.article.exception.CommentException;
import com.chaechae.realworldspringboot.article.exception.CommentExceptionType;
import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.CommentRepository;
import com.chaechae.realworldspringboot.article.repository.FavoriteRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.ArticleCreate;
import com.chaechae.realworldspringboot.article.request.ArticleUpdate;
import com.chaechae.realworldspringboot.article.request.CommentCreate;
import com.chaechae.realworldspringboot.article.request.CommentUpdate;
import com.chaechae.realworldspringboot.article.service.ArticleService;
import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
class ArticleControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ArticleService articleService;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    FollowRepository followRepository;

    @BeforeEach
    void beforeClean() {
        followRepository.deleteAll();
        favoriteRepository.deleteAll();
        commentRepository.deleteAll();
        tagRepository.deleteAll();
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void clean() {
        followRepository.deleteAll();
        favoriteRepository.deleteAll();
        commentRepository.deleteAll();
        tagRepository.deleteAll();
        tokenRepository.deleteAll();
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
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        ArticleCreate create = ArticleCreate.builder().title("??????")
                .description("description")
                .content("??????")
                .build();

        //expected
        mockMvc.perform(post("/articles")
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ?????? ??????")
    public void ?????????_??????_??????_????????????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .description("description")
                .content("??????")
                .tags(tags)
                .build();

        //expected
        mockMvc.perform(post("/articles")
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.title").value("????????? ??????????????????."))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);
        ArticleUpdate articleUpdate = ArticleUpdate.builder().title("?????? ??????").content("?????? ??????").description("description").build();

        //expected
        mockMvc.perform(put("/articles/{articleId}", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleUpdate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        List<Long> savedArticleIdList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ArticleCreate articleCreate = ArticleCreate.builder()
                    .title("?????? " + (i + 1))
                    .content("?????? " + (i + 1))
                    .description("?????? " + (i + 1))
                    .tags(tags)
                    .build();
            savedArticleIdList.add(articleService.createArticle(savedUser.getId(), articleCreate));
        }

        //expected
        mockMvc.perform(get("/articles/{articleId}", savedArticleIdList.get(0))
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedArticleIdList.get(0)))
                .andExpect(jsonPath("$.content").value("?????? 1"))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    public void ?????????_?????????_??????() throws Exception {
        // given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<Article> articles = IntStream.range(1, 31)
                .mapToObj(i -> Article.builder()
                        .title("?????? " + i)
                        .content("?????? " + i)
                        .description("?????? " + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());
        articleRepository.saveAll(articles);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/articles")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", token.getToken())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles.length()", Matchers.is(10)))
                .andExpect(jsonPath("$.articles[0].title").value("?????? 30"))
                .andExpect(jsonPath("$.articles[0].content").value("?????? 30"))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

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

        //expected
        mockMvc.perform(get("/articles")
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10")
                        .param("tag", "tag2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(10)))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    public void ?????????_??????_?????????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

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

        //expected
        mockMvc.perform(get("/articles")
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10")
                        .param("author", String.valueOf(savedUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(5)))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ????????? ??????")
    public void ?????????_??????_?????????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

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

        //expected
        mockMvc.perform(get("/articles")
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10")
                        .param("favorite", String.valueOf(savedUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(2)))
                .andExpect(jsonPath("$.articles[0].content").value(articleRepository.findById(savedArticleIdList.get(1)).get().getContent()))
                .andExpect(jsonPath("$.articles[1].content").value(articleRepository.findById(savedArticleIdList.get(0)).get().getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("??? ?????? ?????? ????????????(??? ??? + ???????????? ??????)")
    public void ??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User user2 = createUser("??????2");
        User user3 = createUser("??????3");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        Follow follow = Follow.builder()
                .follower(savedUser)
                .followed(savedUser3)
                .build();

        followRepository.save(follow);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

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

            ArticleCreate articleCreate3 = ArticleCreate.builder()
                    .title("????????? ?????? " + (i + 1))
                    .content("????????? ?????? " + (i + 1))
                    .description("????????? ?????? " + (i + 1))
                    .tags(tags2)
                    .build();
            savedArticleIdList.add(articleService.createArticle(savedUser3.getId(), articleCreate3));
        }

        //expected
        mockMvc.perform(get("/articles/feed")
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(10)))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ??????")
    public void ?????????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);

        //expected
        mockMvc.perform(delete("/articles/{articleId}", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void ?????????_??????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();

        //expected
        mockMvc.perform(post("/articles/{articleId}/comment", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ??????")
    public void ??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();
        Long savedCommentID = articleService.createComment(savedUser.getId(), articleId, commentCreate);

        //expected
        mockMvc.perform(delete("/articles/{articleId}/comment/{commentId}", articleId, savedCommentID)
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ??????")
    public void ??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();
        Long savedCommentID = articleService.createComment(savedUser.getId(), articleId, commentCreate);

        CommentUpdate commentUpdate = CommentUpdate.builder().content("?????? ??????").build();

        //when
        mockMvc.perform(patch("/articles/{articleId}/comment/{commentId}", articleId, savedCommentID)
                        .header("Authorization", token.getToken())
                        .content(objectMapper.writeValueAsString(commentUpdate))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        Comment updatedComment = commentRepository.findById(savedCommentID).orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        assertThat(updatedComment.getContent()).isEqualTo("?????? ??????");
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    public void ??????_??????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();
        CommentCreate commentCreate2 = CommentCreate.builder().content("??????2").build();
        Long savedCommentID = articleService.createComment(savedUser.getId(), articleId, commentCreate);
        Long savedCommentID2 = articleService.createComment(savedUser.getId(), articleId, commentCreate2);


        //when
        mockMvc.perform(get("/articles/{articleId}/comments", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("??????"))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????????")
    public void ?????????_?????????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);

        //when
        mockMvc.perform(post("/articles/{articleId}/favorite", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertThat(favoriteRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("????????? ??????")
    public void ?????????_??????() throws Exception {
        //given
        User user = createUser("??????1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("??????")
                .description("description")
                .content("??????")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);
        articleService.favoriteCreate(savedUser.getId(), articleId);

        //when
        mockMvc.perform(delete("/articles/{articleId}/favorite", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertThat(favoriteRepository.count()).isEqualTo(0);
    }
}