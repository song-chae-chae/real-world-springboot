package com.chaechae.realworldspringboot.security.jwt.service;

import com.chaechae.realworldspringboot.security.jwt.domain.RefreshToken;
import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.exception.TokenException;
import com.chaechae.realworldspringboot.security.jwt.exception.TokenExceptionType;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.exception.UserException;
import com.chaechae.realworldspringboot.user.exception.UserExceptionType;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Token generateToken(Long uid, String role) {
        long tokenPeriod = 1000L * 60L * 2L;
        long refreshPeriod = 1000L * 60L * 60L * 24L * 30L * 3L;

        Claims claims = Jwts.claims().setSubject(String.valueOf(uid));
        claims.put("role", role);

        Date now = new Date();
        Date refreshExpiredAt = new Date(now.getTime() + refreshPeriod);

        Token token = new Token(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + tokenPeriod))
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact(),
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(refreshExpiredAt)
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact());

        saveRefreshToken(token.getRefreshToken(), uid);

        return token;
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    public Long getUid(String token) {
        String uid = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return Long.parseLong(uid);
    }

    public void saveRefreshToken(String refreshToken, Long uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));
        RefreshToken entity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .build();

        tokenRepository.save(entity);
    }

    public void deleteRefreshToken(String refreshToken) {
        tokenRepository.deleteByRefreshToken(refreshToken);
    }

    @Transactional
    public Token updateRefreshToken(String refreshToken) {
        RefreshToken savedToken = getRefreshToken(refreshToken);

        Long uid = getUid(refreshToken);
        Token newToken = generateToken(uid, "USER");

        savedToken.update(newToken.getRefreshToken());

        return newToken;
    }

    public RefreshToken getRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new TokenException(TokenExceptionType.TOKEN_NOT_FOUND));
    }

    public Token reIssueToken(String refreshToken) {
        RefreshToken refreshTokenEntity = getRefreshToken(refreshToken);
        Long uid = getUid(refreshTokenEntity.getRefreshToken());

        return generateToken(uid, "USER");
    }
}