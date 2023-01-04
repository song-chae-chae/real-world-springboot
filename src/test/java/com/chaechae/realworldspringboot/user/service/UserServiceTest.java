package com.chaechae.realworldspringboot.user.service;

import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.exception.UserException;
import com.chaechae.realworldspringboot.user.exception.UserExceptionType;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.chaechae.realworldspringboot.user.request.UpdateUserRequest;
import com.chaechae.realworldspringboot.user.response.SocialUserResponse;
import com.chaechae.realworldspringboot.user.response.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
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
    @DisplayName("SocialUserResponse를 받아 회원 저장")
    public void 회원_저장_성공() throws Exception {
        //given
        SocialUserResponse socialUserResponse = SocialUserResponse.builder()
                .id("id")
                .image("image")
                .nickname("nickname")
                .email("email")
                .build();

        //when
        User user = userService.join(socialUserResponse);

        //then
        UserResponse userResponse = userService.get(user.getId());
        assertThat(userResponse.getUsername()).isEqualTo("nickname");
        assertThat(userResponse.getEmail()).isEqualTo("email");
    }

    @Test
    @DisplayName("회원 아이디 받아서 조회하기")
    public void 회원_조회_성공() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        //when
        UserResponse userResponse = userService.get(user.getId());

        //then
        assertThat(userResponse.getUsername()).isEqualTo(user.getName());
        assertThat(userResponse.getImage()).isEqualTo(user.getImage());
        assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("없는 회원 아이디 받아서 조회하기")
    public void 회원_조회_실패() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);

        //expected
        UserException userException = assertThrows(UserException.class, () -> {
            userService.get(user.getId() + 1L);
        });

        assertThat(userException.getExceptionType().getMessage()).isEqualTo("회원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("회원 수정하기")
    public void 회원_수정_성공() throws Exception {
        //given
        User user = createUser();
        userRepository.save(user);
        UpdateUserRequest userRequest = UpdateUserRequest.builder()
                .id(user.getId())
                .email("email-update")
                .image("image-update").build();

        //when
        userService.updateUser(userRequest);

        User savedUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

        //then
        Assertions.assertThat(savedUser.getModifiedAt()).isNotEqualTo(user.getModifiedAt());
        assertThat(savedUser.getEmail()).isEqualTo("email-update");
        assertThat(savedUser.getImage()).isEqualTo("image-update");
    }

    @Test
    @DisplayName("잘못된 id를 보내면 회원 수정에 실패한다.")
    public void 회원_수정_실패_id잘못됨() throws Exception {
        //given
        User user = createUser();
        userRepository.save(user);
        UpdateUserRequest userRequest = UpdateUserRequest.builder()
                .id(user.getId() + 1L)
                .email("email-update")
                .image("image-update")
                .build();

        //expected
        assertThrows(UserException.class, () -> {
            userService.updateUser(userRequest);
        });
    }
}