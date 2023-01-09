package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.exception.ArticleException;
import com.chaechae.realworldspringboot.article.exception.ArticleExceptionType;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
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

        Article article = Article.builder()
                .user(savedUser)
                .title("제목")
                .description("설명")
                .content("내용")
                .build();

        //when
        Article savedArticle = articleRepository.save(article);

        //then
        assertThat(savedArticle.getTitle()).isEqualTo(article.getTitle());
        assertThat(savedArticle.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void 게시글_단건_조회() throws Exception {
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

        //when
        Article getArticle = articleRepository.findById(savedArticle.getId())
                .orElseThrow(() -> new ArticleException(ArticleExceptionType.ARTICLE_NOT_FOUND));

        //then
        assertThat(getArticle.getTitle()).isEqualTo(article.getTitle());
        assertThat(savedArticle.getUser().getId()).isEqualTo(savedUser.getId());
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

        Article article = Article.builder()
                .user(savedUser)
                .title("제목")
                .description("설명")
                .content("내용")
                .build();
        Article savedArticle = articleRepository.save(article);

        //when
        articleRepository.deleteById(savedArticle.getId());

        //then
        assertThat(articleRepository.count()).isEqualTo(0);
    }
}