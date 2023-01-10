package com.chaechae.realworldspringboot.profile.repository;

import com.chaechae.realworldspringboot.config.TestConfig;
import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.exception.FollowException;
import com.chaechae.realworldspringboot.profile.exception.FollowExceptionType;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
class FollowRepositoryTest {
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
    @DisplayName("회원1이 회원2 팔로우 성공")
    public void 팔로우_성공() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");

        User save1 = userRepository.save(user1);
        User save2 = userRepository.save(user2);

        //when
        Follow follow = Follow.builder()
                .follower(save1)
                .followed(save2)
                .build();

        Follow savedFollow = followRepository.save(follow);
        Follow following = followRepository
                .findByFollowerIdAndFollowedId(savedFollow.getFollower().getId(), savedFollow.getFollowed().getId())
                .orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND));
        //then
        assertThat(following.getFollowed().getId()).isEqualTo(follow.getFollowed().getId());
        assertThat(following.getFollower().getId()).isEqualTo(follow.getFollower().getId());
    }

    @Test
    @DisplayName("한번 팔로우하면 다시 팔로우하지 못한다.")
    public void 팔로우_중복_불가() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");

        User save1 = userRepository.save(user1);
        User save2 = userRepository.save(user2);

        Follow follow1 = Follow.builder()
                .follower(save1)
                .followed(save2)
                .build();

        Follow follow2 = Follow.builder()
                .follower(save1)
                .followed(save2)
                .build();

        followRepository.save(follow1);

        // expected
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> followRepository.save(follow2));

    }

    @Test
    @DisplayName("회원1이 회원2 언팔로우 성공")
    @Transactional
    public void 언팔로우_성공() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");

        User save1 = userRepository.save(user1);
        User save2 = userRepository.save(user2);

        Follow follow = Follow.builder()
                .follower(save1)
                .followed(save2)
                .build();

        Follow savedFollow = followRepository.save(follow);

        //when
        Follow following = followRepository
                .findByFollowerIdAndFollowedId(savedFollow.getFollower().getId(), savedFollow.getFollowed().getId())
                .orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND));
        followRepository.delete(following);

        //then
        Optional<Follow> deletedFollowing = followRepository.findByFollowerIdAndFollowedId(savedFollow.getFollower().getId(), savedFollow.getFollowed().getId());

        assertThat(deletedFollowing.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("팔로잉 조회")
    public void 팔로잉_조회_성공() throws Exception {
        //given
        User user1 = createUser("회원1");
        User user2 = createUser("회원2");

        User save1 = userRepository.save(user1);
        User save2 = userRepository.save(user2);

        Follow follow = Follow.builder()
                .follower(save1)
                .followed(save2)
                .build();

        Follow savedFollow = followRepository.save(follow);

        //when
        Follow following = followRepository
                .findByFollowerIdAndFollowedId(savedFollow.getFollower().getId(), savedFollow.getFollowed().getId())
                .orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND));
    }
}