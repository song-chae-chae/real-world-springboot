package com.chaechae.realworldspringboot.user.service;

import com.chaechae.realworldspringboot.security.jwt.domain.Token;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.domain.Role;
import com.chaechae.realworldspringboot.user.domain.User;
import com.chaechae.realworldspringboot.user.exception.UserException;
import com.chaechae.realworldspringboot.user.exception.UserExceptionType;
import com.chaechae.realworldspringboot.user.repository.UserRepository;
import com.chaechae.realworldspringboot.user.request.UpdateUserRequest;
import com.chaechae.realworldspringboot.user.response.SocialAuthResponse;
import com.chaechae.realworldspringboot.user.response.SocialUserResponse;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final SocialLoginService socialLoginService;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserLoginResponse doSocialLogin(String authorizationCode) {
        SocialAuthResponse socialAuthResponse = socialLoginService.getAccessToken(authorizationCode);
        SocialUserResponse socialUserResponse = socialLoginService.getUserInfo(socialAuthResponse.getAccess_token());
        User user = userRepository.findBySocialId(socialUserResponse.getId())
                .orElseGet(() -> join(socialUserResponse));

        Token token = tokenService.generateToken(user.getId(), user.getRole().name());

        return new UserLoginResponse(user, token);
    }

    public User join(SocialUserResponse socialUserResponse) {
        User user = User.builder()
                .socialId(socialUserResponse.getId())
                .name(socialUserResponse.getNickname())
                .email(socialUserResponse.getEmail())
                .image(socialUserResponse.getImage())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));
    }

    @Transactional
    public void updateUser(UserLoginResponse authUser, UpdateUserRequest request) {
        if (!authUser.getId().equals(request.getId())) {
            throw new UserException(UserExceptionType.USER_UNAUTHORIZED);
        }

        User savedUser = userRepository.findById(request.getId()).orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));
        savedUser.update(request);
    }
}
