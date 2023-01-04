package com.chaechae.realworldspringboot.user.service;

import com.chaechae.realworldspringboot.user.response.SocialAuthResponse;
import com.chaechae.realworldspringboot.user.response.SocialUserResponse;
import org.springframework.stereotype.Service;

@Service
public interface SocialLoginService {
    SocialAuthResponse getAccessToken(String authorizationCode);

    SocialUserResponse getUserInfo(String accessToken);
}
