package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.ArticleCreate;
import com.chaechae.realworldspringboot.article.request.ArticleUpdate;
import com.chaechae.realworldspringboot.article.service.ArticleService;
import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    void beforeClean() {
        tagRepository.deleteAll();
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void clean() {
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
    @DisplayName("게시글 작성")
    public void 게시글_작성_성공() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        ArticleCreate create = ArticleCreate.builder().title("제목")
                .description("description")
                .content("내용")
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
    @DisplayName("게시글 작성 실패 - 제목 없음")
    public void 게시글_작성_실패_제목없음() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .description("description")
                .content("내용")
                .tags(tags)
                .build();
        System.out.println("tlqkf : " + objectMapper.writeValueAsString(create));
        //expected
        mockMvc.perform(post("/articles")
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정")
    public void 게시글_수정_성공() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("제목")
                .description("description")
                .content("내용")
                .tags(tags)
                .build();
        Long articleId = articleService.createArticle(savedUser.getId(), create);
        ArticleUpdate articleUpdate = ArticleUpdate.builder().title("제목 변경").content("내용 변경").description("description").build();

        //expected
        mockMvc.perform(put("/articles/{articleId}", articleId)
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleUpdate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void 게시글_단건_조회() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("게시글 리스트 조회")
    public void 게시글_리스트_조회() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("게시글 삭제")
    public void 게시글_삭제_성공() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");

        ArticleCreate create = ArticleCreate.builder()
                .title("제목")
                .description("description")
                .content("내용")
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
}