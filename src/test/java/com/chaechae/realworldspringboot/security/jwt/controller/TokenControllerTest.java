package com.chaechae.realworldspringboot.security.jwt.controller;

import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.chaechae.realworldspringboot.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class TokenControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    void clean() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void afterClean() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("리프레시 토큰을 삭제한다.")
    public void 리프레시_토큰_삭제_성공() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("잘못된 리프레시 토큰을 보내면 토큰을 삭제할 수 없다.")
    public void 리프레시_토큰_삭제_실패_잘못된토큰() throws Exception {
        //given

        //when

        //then
    }
}