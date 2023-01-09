package com.chaechae.realworldspringboot.profile.controller;

import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.exception.FollowException;
import com.chaechae.realworldspringboot.profile.exception.FollowExceptionType;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
import com.chaechae.realworldspringboot.profile.request.FollowUser;
import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.repository.TokenRepository;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ProfileControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void clean() {
        followRepository.deleteAll();
        tokenRepository.deleteAll();
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
    @DisplayName("팔로잉")
//    @WithMockUser
    public void 팔로잉_요청() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Token token = tokenService.generateToken(user1.getId(), "USER");

        FollowUser followUser = FollowUser.builder().id(savedUser2.getId()).build();

        //expected
        mockMvc.perform(post(("/profiles/follow"))
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followUser)))
                .andExpect(status().isOk())
                .andDo(print());

        Follow following = followRepository.findByFollowerIdAndFollowedId(savedUser1.getId(), savedUser2.getId())
                .orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND));

        assertThat(following.getFollower().getId()).isEqualTo(savedUser1.getId());
        assertThat(following.getFollowed().getId()).isEqualTo(savedUser2.getId());
    }

    @Test
    @DisplayName("언팔로잉")
    public void 언팔로잉_요청() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Token token = tokenService.generateToken(savedUser1.getId(), "USER");

        Follow follow = Follow.builder().follower(savedUser1).followed(savedUser2).build();
        followRepository.save(follow);

        FollowUser followUser = FollowUser.builder().id(savedUser2.getId()).build();

        //expected
        mockMvc.perform(delete("/profiles/follow")
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followUser)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("프로필 조회")
    @WithMockUser
    public void 프로필_조회_성공_팔로잉X() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Token token = tokenService.generateToken(savedUser1.getId(), "USER");

        FollowUser followUser = FollowUser.builder().id(savedUser2.getId()).build();

        //expected
        mockMvc.perform(get("/profiles/{userId}", savedUser2.getId())
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.following").value(false))
                .andExpect(jsonPath("$.id").value(savedUser2.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("프로필 조회")
    @WithMockUser
    public void 프로필_조회_성공_팔로잉() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Token token = tokenService.generateToken(savedUser1.getId(), "USER");

        Follow follow = Follow.builder().follower(savedUser1).followed(savedUser2).build();
        followRepository.save(follow);

        FollowUser followUser = FollowUser.builder().id(savedUser2.getId()).build();

        //expected
        mockMvc.perform(get("/profiles/{userId}", savedUser2.getId())
                        .header("Authorization", token.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.following").value(true))
                .andExpect(jsonPath("$.id").value(savedUser2.getId()))
                .andDo(print());
    }
}