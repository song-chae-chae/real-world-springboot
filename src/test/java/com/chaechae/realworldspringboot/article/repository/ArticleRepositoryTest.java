package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Favorite;
import com.chaechae.realworldspringboot.article.domain.Tag;
import com.chaechae.realworldspringboot.article.exception.ArticleException;
import com.chaechae.realworldspringboot.article.exception.ArticleExceptionType;
import com.chaechae.realworldspringboot.article.request.ArticleSearch;
import com.chaechae.realworldspringboot.config.TestConfig;
import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
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
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        favoriteRepository.deleteAll();
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void afterClean() {
        favoriteRepository.deleteAll();
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
        Page<Article> pageArticles = articleRepository.getList(articleSearch);
        List<Article> posts = pageArticles.getContent();

        // then
        assertThat(posts.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("태그로 게시글 조회")
    public void 게시글_조회_태그() throws Exception {
        // given
        User user = createUser("회원1");
        User savedUser = userRepository.save(user);

        Article article = Article.builder()
                .title("제목")
                .content("내용")
                .description("설명")
                .build();

        Article savedArticle = articleRepository.save(article);

        Tag tag = Tag.builder()
                .tagName("tag1").build();

        tag.setArticle(savedArticle);

        tagRepository.save(tag);

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .tag("tag1")
                .build();
        // when
        Page<Article> pageArticles = articleRepository.getArticleListByTag(articleSearch);
        List<Article> articles = pageArticles.getContent();

        // then
        assertThat(articles.size()).isEqualTo(1);

        Set<Tag> savedTag = articles.get(0).getTags();
        List<Tag> tags = new ArrayList<>(savedTag);

        assertThat(tags.get(0).getTagName()).isEqualTo("tag1");
    }

    @Test
    @DisplayName("글쓴이로 게시글 조회")
    public void 게시글_조회_글쓴이() throws Exception {
        // given
        User user = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);

        Article article = Article.builder()
                .title("제목")
                .content("내용")
                .description("설명")
                .user(savedUser)
                .build();

        Article article2 = Article.builder()
                .title("제목2")
                .content("내용2")
                .description("설명2")
                .user(savedUser2)
                .build();

        Article savedArticle = articleRepository.save(article);
        Article savedArticle2 = articleRepository.save(article2);

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .author(savedUser.getId())
                .build();
        // when
        Page<Article> pageArticles = articleRepository.getArticleListByAuthor(articleSearch);
        List<Article> articles = pageArticles.getContent();

        // then
        assertThat(articles.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요한 게시글 조회")
    public void 게시글_조회_좋아요() throws Exception {
        // given
        User user = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        List<Article> savedArticleList = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            Article article = Article.builder()
                    .title("제목 " + i)
                    .content("내용 " + i)
                    .description("설명 " + i)
                    .user(savedUser)
                    .build();
            savedArticleList.add(articleRepository.save(article));

            Article article2 = Article.builder()
                    .title("제목2 " + i)
                    .content("내용2 " + i)
                    .description("설명2 " + i)
                    .user(savedUser2)
                    .build();
            savedArticleList.add(articleRepository.save(article2));
        }

        Favorite favorite = Favorite.builder().user(savedUser).build();
        favorite.setArticle(savedArticleList.get(0));
        favoriteRepository.save(favorite);

        Favorite favorite2 = Favorite.builder().user(savedUser).build();
        favorite2.setArticle(savedArticleList.get(1));
        favoriteRepository.save(favorite2);

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .favorite(savedUser.getId())
                .build();
        // when
        Page<Article> pageArticles = articleRepository.getArticleListByUserFavorite(articleSearch);
        List<Article> articles = pageArticles.getContent();

        // then
        assertThat(articles.size()).isEqualTo(2);
//        assertThat(articles.get(0).getId()).isEqualTo(savedArticleList.get(0).getId());
//        assertThat(articles.get(1).getId()).isEqualTo(savedArticleList.get(1).getId());
    }

    @Test
    @DisplayName("피드 조회")
    public void 피드_조회() throws Exception {
        // given
        User user = createUser("회원1");
        User user2 = createUser("회원2");
        User user3 = createUser("회원3");

        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        followRepository.save(Follow.builder().follower(savedUser).followed(savedUser2).build());

        for (int i = 1; i < 6; i++) {
            Article article = Article.builder()
                    .title("제목 " + i)
                    .content("내용 " + i)
                    .description("설명 " + i)
                    .user(savedUser)
                    .build();
            articleRepository.save(article);

            Article article2 = Article.builder()
                    .title("제목2 " + i)
                    .content("내용2 " + i)
                    .description("설명2 " + i)
                    .user(savedUser2)
                    .build();
            articleRepository.save(article2);

            Article article3 = Article.builder()
                    .title("제목3 " + i)
                    .content("내용3 " + i)
                    .description("설명3 " + i)
                    .user(savedUser3)
                    .build();
            articleRepository.save(article3);
        }

        ArticleSearch articleSearch = ArticleSearch.builder()
                .page(1)
                .size(10)
                .build();

        // when
        Page<Article> pageArticles = articleRepository.getFeed(savedUser.getId(), articleSearch);
        List<Article> articles = pageArticles.getContent();

        // then
        assertThat(articles.size()).isEqualTo(10);
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