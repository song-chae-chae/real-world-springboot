package com.chaechae.realworldspringboot.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Getter
@NoArgsConstructor
@ToString
public class KakaoAccessTokenRequest {
    private final String grant_type = "authorization_code";
    private String client_id;
    private String redirect_url;
    private String code;

    @Builder
    public KakaoAccessTokenRequest(@Value("${kakao.client-id}")String client_id, String redirect_url, String code) {
        this.client_id = client_id;
        this.redirect_url = redirect_url;
        this.code = code;
    }
}
