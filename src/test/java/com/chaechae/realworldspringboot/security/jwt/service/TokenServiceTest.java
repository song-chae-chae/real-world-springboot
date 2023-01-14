package com.chaechae.realworldspringboot.security.jwt.service;

import com.chaechae.realworldspringboot.article.repository.ArticleRepository;
import com.chaechae.realworldspringboot.security.jwt.domain.RefreshToken;
import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.exception.TokenException;
import com.chaechae.realworldspringboot.security.jwt.exception.TokenExceptionType;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TokenServiceTest {
    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Value("${jwt.secret-key}")
    String secretKey;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();

        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @AfterEach
    void afterClean() {
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser() {
        return User.builder()
                .name("name")
                .email("email")
                .image("image")
                .socialId("socialId")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("아이디와 권한을 받아 토큰을 생성한다.")
    public void 토큰_생성_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.getToken()).getBody();
        String role = claims.get("role").toString();
        String uid = claims.getSubject();

        //expected
        assertThat(role).isEqualTo("USER");
        assertThat(uid).isEqualTo(String.valueOf(user.getId()));
    }

    @Test
    @DisplayName("유효한 토큰을 검증하면 true")
    public void 토큰_검증_유효함() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        //when
        boolean isValid = tokenService.verifyToken(token.getToken());

        //then
        assertThat(isValid).isEqualTo(true);
    }

    @Test
    @DisplayName("유효기간 만료 토큰 검증을 검증하면 false")
    public void 토큰_검증_유효기간만료() throws Exception {
        //given
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject("1");
        claims.put("role", "USER");
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() - 300000))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();


        //when
        boolean isValid = tokenService.verifyToken(token);

        //then
        assertThat(isValid).isEqualTo(false);
    }


    @Test
    @DisplayName("uid 파싱")
    public void UID_파싱_성공() throws Exception {
        //given
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject("1");
        claims.put("role", "USER");
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + 30000))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();

        //when
        Long uid = tokenService.getUid(token);

        //then
        Assertions.assertThat(uid).isEqualTo(1L);
    }

    @Test
    @DisplayName("잘못된 토큰을 파싱하려 하면 실패한다.")
    public void UID_파싱_실패() throws Exception {
        //given
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject("1");
        claims.put("role", "USER");
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() - 3000))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();

        //expected
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            tokenService.getUid("123123adf" + token);
        });
    }

    @Test
    @DisplayName("리프레시 토큰 저장")
    public void 리프레시_토큰_저장_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        //when
        RefreshToken savedRefreshTokenEntity = tokenRepository.findByRefreshToken(token.getRefreshToken()).orElseThrow(() -> new TokenException(TokenExceptionType.TOKEN_NOT_FOUND));

        //then
        Assertions.assertThat(savedRefreshTokenEntity.getRefreshToken()).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    @Transactional
    public void 리프레시_토큰_삭제_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        //when
        tokenRepository.deleteByRefreshToken(token.getRefreshToken());

        //then
        assertThrows(TokenException.class, () -> {
            tokenRepository.findByRefreshToken(token.getRefreshToken()).orElseThrow(() -> new TokenException(TokenExceptionType.TOKEN_NOT_FOUND));
        });

    }

    @Test
    @DisplayName("리프레시 토큰 조회")
    public void 리프레시_토큰_조회_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        //when
        RefreshToken refreshTokenEntity = tokenRepository.findByRefreshToken(token.getRefreshToken()).orElseThrow(() -> new TokenException(TokenExceptionType.TOKEN_NOT_FOUND));

        //then
        Assertions.assertThat(refreshTokenEntity.getRefreshToken()).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("토큰 재발행")
    public void 토큰_재발행_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        Token token = tokenService.generateToken(savedUser.getId(), "USER");
        Thread.sleep(1000);
        //when
        Token reIssueToken = tokenService.reIssueToken(token.getRefreshToken());

        //then
        assertThat(reIssueToken.getToken()).isNotEqualTo(token.getToken());
        assertThat(reIssueToken.getRefreshToken()).isNotEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("토큰 재발행 실패")
    public void 토큰_재발행_실패_잘못된토큰() throws Exception {
        //given

        //when

        //then
    }
}