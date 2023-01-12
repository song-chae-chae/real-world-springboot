package com.chaechae.realworldspringboot.article.repository;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Comment;
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

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        commentRepository.deleteAll();
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
    @DisplayName("댓글 저장")
    public void 댓글_저장() throws Exception {
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

        Comment comment = Comment.builder()
                .user(savedUser)
                .article(savedArticle)
                .content("댓글").build();
        //when
        Comment savedComment = commentRepository.save(comment);

        //then
        assertThat(savedComment.getContent()).isEqualTo("댓글");
        assertThat(savedComment.getUser().getName()).isEqualTo("회원1");
        assertThat(savedComment.getArticle().getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("댓글 삭제")
    public void 댓글_삭제() throws Exception {
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

        Comment comment = Comment.builder()
                .user(savedUser)
                .article(savedArticle)
                .content("댓글").build();
        Comment savedComment = commentRepository.save(comment);

        //when
        commentRepository.deleteById(savedComment.getId());

        //then
        assertThat(commentRepository.count()).isEqualTo(0);
    }
    @Test
    @DisplayName("아티클 아이디로 댓글 조회")
    public void 댓글_조회_아티클아이디() throws Exception {
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

        Comment comment = Comment.builder()
                .user(savedUser)
                .article(savedArticle)
                .content("댓글").build();
        Comment comment2 = Comment.builder()
                .user(savedUser)
                .article(savedArticle)
                .content("댓글2").build();
        Comment savedComment = commentRepository.save(comment);
        Comment savedComment2 = commentRepository.save(comment2);

        //when
        List<Comment> byArticleId = commentRepository.findByArticleId(savedArticle.getId());

        //then
        assertThat(byArticleId.size()).isEqualTo(2);
        assertThat(byArticleId.get(0).getContent()).isEqualTo("댓글");
    }
}