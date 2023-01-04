package com.chaechae.realworldspringboot.user.service;

import com.chaechae.realworldspringboot.user.response.KakaoUserInfoResponse;
import com.chaechae.realworldspringboot.user.response.SocialAuthResponse;
import com.chaechae.realworldspringboot.user.response.SocialUserResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSocialLoginServiceImpl implements SocialLoginService {
    private final RestTemplate restTemplate;
    private final String KAKAO_API = "https://kauth.kakao.com";

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirectURI;

    @Override
    public SocialAuthResponse getAccessToken(String authorizationCode) {
        ResponseEntity<String> kakaoAccessTokenResponseResponseEntity = restTemplate.postForEntity(KAKAO_API + "/oauth/token", getParams(authorizationCode), String.class);
        Gson gson = new Gson();
        return gson.fromJson(kakaoAccessTokenResponseResponseEntity.getBody(), SocialAuthResponse.class);
    }

    private LinkedMultiValueMap<String, String> getParams(String authorizationCode) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectURI);
        params.add("code", authorizationCode);
        return params;
    }

    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + accessToken);

        var request = new HttpEntity(header);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                String.class
        );
        Gson gson = new Gson();
        KakaoUserInfoResponse kakaoUserInfoResponse = gson.fromJson(response.getBody(), KakaoUserInfoResponse.class);

        return SocialUserResponse.builder()
                .id(kakaoUserInfoResponse.getId())
                .nickname(kakaoUserInfoResponse.getKakao_account().getProfile().getNickname())
                .email(kakaoUserInfoResponse.getKakao_account().getEmail())
                .image(kakaoUserInfoResponse.getKakao_account().getProfile().getProfile_image_url())
                .build();
    }
}
