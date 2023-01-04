package com.chaechae.realworldspringboot.user.controller;

import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.chaechae.realworldspringboot.user.request.SocialLoginRequest;
import com.chaechae.realworldspringboot.user.request.UpdateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TokenService tokenService;

    @BeforeEach
    void clean() {
        tokenRepository.deleteAll();
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
    @DisplayName("잘못된 인가 코드로 로그인 실패 시 오류 메시지 반환")
    public void 로그인_실패_잘못된인가코드() throws Exception {
        //given
        SocialLoginRequest request = SocialLoginRequest.builder()
                .code("kara")
                .build();

        //expected
        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 REST API 요청입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("id 없으면 회원 수정에 실패한다.")
    public void 회원_수정_실패_id없음() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        UpdateUserRequest userRequest = UpdateUserRequest.builder()
                .email("email@email.com")
                .image("image-update")
                .build();

        //expected
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.id").value("id를 입력해주세요."))

                .andDo(print());
    }

    @Test
    @DisplayName("이메일 없으면 회원 수정에 실패한다.")
    public void 회원_수정_실패_이메일없음() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        UpdateUserRequest userRequest = UpdateUserRequest.builder()
                .id(savedUser.getId())
                .image("image-update")
                .build();

        //expected
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("email을 입력해주세요."))

                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 이메일 형식이면 회원 수정에 실패한다.")
    public void 회원_수정_실패_이메일양식잘못됨() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        UpdateUserRequest userRequest = UpdateUserRequest.builder()
                .id(savedUser.getId())
                .email("email-update")
                .image("image-update")
                .build();

        //expected
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("올바른 email을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("이미지 없으면 회원 수정에 실패한다.")
    public void 회원_수정_실패_이미지없음() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        UpdateUserRequest userRequest = UpdateUserRequest.builder()
                .id(savedUser.getId())
                .email("email@email.com")
                .build();

        //expected
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.image").value("image를 입력해주세요."))

                .andDo(print());
    }

    @Test
    @DisplayName("회원 조회 성공")
    public void 회원_조회_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);
        Token token = tokenService.generateToken(savedUser.getId(), "USER");

        //expedted
        mockMvc.perform(get("/user")
                        .header("Authorization", token.getToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

}