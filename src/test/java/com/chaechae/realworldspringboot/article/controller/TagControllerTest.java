package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.ArticleCreate;
import com.chaechae.realworldspringboot.article.service.ArticleService;
import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TagControllerTest {
    @Autowired
    MockMvc mockMvc;

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
    @DisplayName("태그 목록 조회")
    public void 태그_목록_조회() throws Exception {
        //given
        User user = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");
        Token token2 = tokenService.generateToken(savedUser2.getId(), "USER");

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        ArticleCreate create = ArticleCreate.builder().title("제목")
                .description("description")
                .content("내용")
                .tags(tags)
                .build();

        List<String> tags2 = new ArrayList<>();
        tags.add("tag2");
        tags.add("tag3");

        ArticleCreate create2 = ArticleCreate.builder().title("제목2")
                .description("description")
                .content("내용")
                .tags(tags2)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), create);
        Long savedArticleId2 = articleService.createArticle(savedUser2.getId(), create2);

        //expected
        mockMvc.perform(get("/tags")
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(print());

        assertThat(tagRepository.count()).isEqualTo(4);
    }
}