package com.chaechae.realworldspringboot.profile.service;

import com.chaechae.realworldspringboot.profile.domain.Follow;
import com.chaechae.realworldspringboot.profile.exception.FollowException;
import com.chaechae.realworldspringboot.profile.exception.FollowExceptionType;
import com.chaechae.realworldspringboot.profile.repository.FollowRepository;
import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.exception.UserException;
import com.chaechae.realworldspringboot.user.exception.UserExceptionType;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void follow(Long followerId, Long followedId) {
        User follower = findUser(followerId);
        User followed = findUser(followedId);

        if (followRepository.findByFollowerIdAndFollowedId(followerId, followedId).isEmpty()) {
            followRepository.save(Follow.builder().follower(follower).followed(followed).build());
            return;
        }

        throw new FollowException(FollowExceptionType.ALREADY_FOLLOWED);
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));
    }

    public void unFollow(Long followerId, Long followedId) {
        Follow following = followRepository.findByFollowerIdAndFollowedId(followerId, followedId).orElseThrow(() -> new FollowException(FollowExceptionType.FOLLOWING_NOT_FOUND));

        followRepository.delete(following);
    }

    public ProfileResponse get(Long authId, Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

        return ProfileResponse.builder()
                .user(findUser)
                .following(isFollowed(authId, userId))
                .build();
    }

    public boolean isFollowed(Long authId, Long userId) {
        if (authId == null) {
            return false;
        }
        return followRepository.findByFollowerIdAndFollowedId(authId, userId).isPresent();
    }
}
