package com.chaechae.realworldspringboot.article.service;

import com.chaechae.realworldspringboot.article.domain.Article;
import com.chaechae.realworldspringboot.article.domain.Comment;
import com.chaechae.realworldspringboot.article.domain.Tag;
import com.chaechae.realworldspringboot.article.exception.ArticleException;
import com.chaechae.realworldspringboot.article.exception.CommentException;
import com.chaechae.realworldspringboot.article.exception.CommentExceptionType;
import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.article.repository.CommentRepository;
import com.chaechae.realworldspringboot.article.repository.TagRepository;
import com.chaechae.realworldspringboot.article.request.*;
import com.chaechae.realworldspringboot.article.response.ArticleResponse;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
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

    @BeforeEach
    void clean() {
        commentRepository.deleteAll();
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성")
    public void 게시글_작성_성공() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
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
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();
        //when
        articleService.createArticle(savedUser.getId(), articleCreate);

        //then
        assertThat(articleRepository.count()).isEqualTo(1);
        assertThat(articleRepository.findAll().get(0).getTitle()).isEqualTo("제목");
        assertThat(tagRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 수정")
    @Transactional
    public void 게시글_수정_성공() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        Article savedArticle = articleRepository.findById(savedArticleId).get();

        //when
        Set<Tag> updateTags = new HashSet<>();
        updateTags.add(Tag.builder().tagName("tag1").build());
        updateTags.add(Tag.builder().tagName("tag2").build());

        ArticleUpdate articleUpdate = ArticleUpdate.builder().title("제목-update").description("설명- update").content("내용-update").tags(updateTags).build();
        articleService.updateArticle(savedUser.getId(), savedArticle.getId(), articleUpdate);
        Article updatedArticle = articleRepository.findById(savedArticleId).get();

        //then
        assertThat(articleRepository.findAll().get(0).getTitle()).isEqualTo("제목-update");
        assertThat(new ArrayList<>(updatedArticle.getTags()).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 단건 조회")
    @Transactional
    public void 게시글_단건_조회() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        //when
        Article article = articleService.get(savedArticleId);

        //then
        assertThat(article.getTitle()).isEqualTo("제목");
        assertThat(article.getContent()).isEqualTo("내용");
        assertThat(new ArrayList<>(article.getTags()).get(0).getTagName()).isEqualTo("tag");
    }

    @Test
    @DisplayName("게시글 목록 조회")
    public void 게시글_목록_최신순조회() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
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
                .size(10).build();


        //when
        List<ArticleResponse> list = articleService.getList(savedUser.getId(), articleSearch);

        //then
        assertThat(list.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("게시글 삭제")
    public void 게시글_삭제() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        //when
        articleService.deleteArticle(savedUser.getId(), savedArticleId);

        //then
        assertThrows(ArticleException.class, () -> {
            articleService.get(savedArticleId);
        });
    }

    @Test
    @DisplayName("게시글 댓글 작성")
    public void 게시글_댓글_작성() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder()
                .content("댓글")
                .build();
        //when
        Long commentId = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate);

        //then
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));
        assertThat(comment.getContent()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("댓글 삭제")
    public void 댓글_삭제() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder()
                .content("댓글")
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
    @DisplayName("댓글 수정")
    public void 댓글_수정() throws Exception {
        //given
        User user = User.builder()
                .name("회원1")
                .role(Role.USER)
                .image("image")
                .socialId("socialId")
                .email("email")
                .build();
        User savedUser = userRepository.save(user);

        List<String> tags = new ArrayList<>();
        tags.add("tag");

        ArticleCreate articleCreate = ArticleCreate.builder()
                .title("제목")
                .description("설명")
                .content("내용")
                .tags(tags)
                .build();

        Long savedArticleId = articleService.createArticle(savedUser.getId(), articleCreate);

        CommentCreate commentCreate = CommentCreate.builder()
                .content("댓글")
                .build();
        Long commentId = articleService.createComment(savedUser.getId(), savedArticleId, commentCreate);

        //when
        CommentUpdate commentUpdate = CommentUpdate.builder().content("댓글 수정").build();

        articleService.updateComment(savedUser.getId(), commentId, commentUpdate);

        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));
        assertThat(findComment.getContent()).isEqualTo("댓글 수정");
    }
}