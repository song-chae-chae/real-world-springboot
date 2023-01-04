package com.chaechae.realworldspringboot.security.jwt.filter;

import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.exception.TokenException;
import com.chaechae.realworldspringboot.security.jwt.exception.TokenExceptionType;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader("Authorization") != null || request.getHeader("Refresh") != null) {
            Token token = getAccessToken(request);
            Long userId = tokenService.getUid(token.getToken());
            UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                    .id(userId)
                    .build();

            Authentication auth = getAuthentication(userLoginResponse);
            SecurityContextHolder.getContext().setAuthentication(auth);

            response.setHeader("Authorization", token.getToken());
            response.setHeader("Refresh", token.getRefreshToken());
        }

        filterChain.doFilter(request, response);
    }

    private Token getAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh");

        if (accessToken == null) {
            throw new TokenException(TokenExceptionType.TOKEN_INVALID);
        }

        if (tokenService.verifyToken(accessToken)) {
            return Token.builder()
                    .token(accessToken)
                    .build();
        }

        if (refreshToken == null) {
            throw new TokenException(TokenExceptionType.TOKEN_INVALID);
        }

        return tokenService.reIssueToken(refreshToken);
    }

    public Authentication getAuthentication(UserLoginResponse member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}