package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.exception.ArticleException;
import com.chaechae.realworldspringboot.article.exception.ArticleExceptionType;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;
import com.chaechae.realworldspringboot.config.TestConfig;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
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
        // given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);

        List<Article> articles = IntStream.range(1, 31)
                .mapToObj(i -> Article.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .description("설명 " + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());
        articleRepository.saveAll(articles);

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .build();
        // when
        List<Article> posts = articleRepository.getList(articleSearch);

        // then
        assertThat(posts.size()).isEqualTo(10);
        assertThat(posts.get(0).getTitle()).isEqualTo("제목 30");
        assertThat(posts.get(4).getTitle()).isEqualTo("제목 26");
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