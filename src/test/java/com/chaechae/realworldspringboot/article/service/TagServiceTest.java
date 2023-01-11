package com.chaechae.realworldspringboot.article.service;

import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.ArticleCreate;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TagServiceTest {
    @Autowired
    ArticleService articleService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagService tagService;

    @BeforeEach
    void clean() {
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
    @DisplayName("태그 목록 조회")
    public void 태그_목록_조회() throws Exception {
        //given
        User user = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);


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

        List<String> tagNameList = tagService.getTagNameList();

        assertThat(tagNameList.size()).isEqualTo(3);
        assertThat(tagNameList.contains("tag2")).isEqualTo(true);
        assertThat(tagRepository.count()).isEqualTo(4);
    }
}