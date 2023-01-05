package com.chaechae.realworldspringboot.profile.service;

import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.exception.FollowException;
import com.chaechae.realworldspringboot.profile.exception.FollowExceptionType;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProfileServiceTest {
    @Autowired
    ProfileService profileService;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        followRepository.deleteAll();
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
    @DisplayName("회원1이 회원2 팔로우")
    public void 팔로우_성공() throws Exception {
        // given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        // when
        profileService.follow(user1.getId(), user2.getId());
        Follow following = followRepository.findByFollowerIdAndFollowedId(user1.getId(), user2.getId()).orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND));

        // then
        assertThat(following.getFollowed().getId()).isEqualTo(savedUser2.getId());
        assertThat(following.getFollower().getId()).isEqualTo(savedUser1.getId());
    }

    @Test
    @DisplayName("회원1이 회원2 언팔로우")
    @Transactional
    public void 언팔로우_성공() throws Exception {
        // given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        profileService.follow(user1.getId(), user2.getId());

        // when
        profileService.unFollow(user1.getId(), user2.getId());

        // then
        assertThrows(FollowException.class,
                () -> followRepository.findByFollowerIdAndFollowedId(savedUser1.getId(), savedUser2.getId())
                        .orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND)));
    }

    @Test
    @DisplayName("회원1이 팔로우하지 않은 회원2 프로필 조회")
    public void 프로필_조회_팔로우X() throws Exception {
        // given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        // when
        ProfileResponse profileResponse = profileService.get(savedUser1.getId(), savedUser2.getId());

        // then
        assertThat(profileResponse.getId()).isEqualTo(savedUser2.getId());
        assertThat(profileResponse.isFollowing()).isEqualTo(false);
    }

    @Test
    @DisplayName("회원1이 팔로우한 회원2 프로필 조회")
    public void 프로필_조회_팔로우() throws Exception {
        // given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        profileService.follow(savedUser1.getId(), savedUser2.getId());

        // when
        ProfileResponse profileResponse = profileService.get(savedUser1.getId(), savedUser2.getId());

        // then
        assertThat(profileResponse.getId()).isEqualTo(savedUser2.getId());
        assertThat(profileResponse.isFollowing()).isEqualTo(true);
    }

    @Test
    @DisplayName("회원1이 회원2 중복 팔로우 시도")
    public void 팔로우_중복() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        profileService.follow(savedUser1.getId(), savedUser2.getId());

        //when
        FollowException followException = assertThrows(FollowException.class, () -> profileService.follow(savedUser1.getId(), savedUser2.getId()));

        //then
        assertThat(followException.getExceptionType()).isEqualTo(FollowExceptionType.ALREADY_FOLLOWED);
    }

    @Test
    @DisplayName("팔로우 하지 않은 회원 언팔로우")
    public void 언팔로우_실패_팔로우안함() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        //when
        FollowException followException = assertThrows(FollowException.class, () -> profileService.unFollow(savedUser1.getId(), savedUser2.getId()));

        //then
        assertThat(followException.getExceptionType()).isEqualTo(FollowExceptionType.FOLLOWING_NOT_FOUND);
    }
}