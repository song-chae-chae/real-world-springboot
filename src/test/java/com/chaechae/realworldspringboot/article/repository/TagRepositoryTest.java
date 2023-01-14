package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Tag;
import com.chaechae.realworldspringboot.config.TestConfig;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }
    @AfterEach
    void afterClean() {
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
    @DisplayName("태그 저장")
    public void 태그_저장_성공() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);

        Article article = Article.builder()
                .user(savedUser)
                .title("제목")
                .description("설명")
                .content("내용")
                .build();
        Article savedArticle = articleRepository.save(article);

        Tag tag = Tag.builder().tagName("tagName").build();
        tag.setArticle(savedArticle);

        //when
        Tag savedTag = tagRepository.save(tag);

        //then
        assertThat(savedTag.getTagName()).isEqualTo("tagName");
    }

    @Test
    @DisplayName("태그 삭제")
    public void 태그_삭제_성공() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);

        Article article = Article.builder()
                .user(savedUser)
                .title("제목")
                .description("설명")
                .content("내용")
                .build();
        Article savedArticle = articleRepository.save(article);

        Tag tag = Tag.builder().tagName("tagName").build();
        tag.setArticle(savedArticle);
        Tag savedTag = tagRepository.save(tag);

        //when
        tagRepository.deleteByArticleId(savedArticle.getId());

        //then
        assertThat(tagRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("아티클 id로 태그 조회")
    public void 태그_조회_성공_아티클id() throws Exception {
        //given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);

        Article article = Article.builder()
                .user(savedUser)
                .title("제목")
                .description("설명")
                .content("내용")
                .build();

        Article article2 = Article.builder()
                .user(savedUser)
                .title("제목2")
                .description("설명2")
                .content("내용2")
                .build();

        Article savedArticle = articleRepository.save(article);
        Article savedArticle2 = articleRepository.save(article2);

        Tag tag1 = Tag.builder().tagName("태그1").build();
        Tag tag2 = Tag.builder().tagName("태그2").build();
        tag1.setArticle(savedArticle);
        tag2.setArticle(savedArticle);

        Tag tag3 = Tag.builder().tagName("태그3").build();
        tag3.setArticle(savedArticle2);

        Tag savedTag1 = tagRepository.save(tag1);
        Tag savedTag2 = tagRepository.save(tag2);

        Tag savedTag3 = tagRepository.save(tag3);

        //when
        List<Tag> byArticleId1 = tagRepository.findByArticleId(savedArticle.getId());
        List<Tag> byArticleId2 = tagRepository.findByArticleId(savedArticle2.getId());

        //then
        assertThat(byArticleId1.size()).isEqualTo(2);
        assertThat(byArticleId2.size()).isEqualTo(1);
    }
}