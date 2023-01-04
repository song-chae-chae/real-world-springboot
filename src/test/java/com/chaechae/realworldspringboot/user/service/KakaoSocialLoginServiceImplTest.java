package com.chaechae.realworldspringboot.user.service;

import com.chaechae.realworldspringboot.base.exception.RestTemplateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@SpringBootTest
class KakaoSocialLoginServiceImplTest {
    @Autowired
    KakaoSocialLoginServiceImpl kakaoSocialLoginService;

    @Test
    @DisplayName("인가 코드를 보내 액세스 토큰 얻기")
    public void 액세스_토큰_받기_성공() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("잘못된 인가 코드를 보내면 액세스 토큰을 받지 못한다.")
    public void 액세스_토큰_받기_실패_잘못된인가코드() throws Exception {
        //given
        String invalidAuthorizationCode = "kara";

        //expected
        Assertions.assertThrows(RestTemplateException.class, () -> {
            kakaoSocialLoginService.getAccessToken(invalidAuthorizationCode);
        });
    }

    @Test
    @DisplayName("중복된 인가 코드를 보내면 액세스 토큰을 받지 못한다.")
    public void 액세스_토큰_받기_실패_중복인가코드() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("액세스 토큰을 보내서 사용자 정보를 가져온다.")
    public void 사용자_정보_조회_성공() throws Exception {
        //given

        //when

        //then
    }


    @Test
    @DisplayName("잘못된 access_token으로 사용자 정보 조회하기")
    public void 사용자_정보_조회_실패_잘못된액세스토큰() throws Exception {
        //given
        String invalidAccessToken = "kara";

        //expected
        Assertions.assertThrows(RestTemplateException.class, () -> {
            kakaoSocialLoginService.getUserInfo(invalidAccessToken);
        });
    }
}