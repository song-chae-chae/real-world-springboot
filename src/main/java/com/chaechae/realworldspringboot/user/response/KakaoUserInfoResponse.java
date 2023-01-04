package com.chaechae.realworldspringboot.user.response;

import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class KakaoUserInfoResponse {
    private String id;
    private KakaoAccount kakao_account;
    private boolean has_signed_up;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class KakaoAccount {
        private Profile profile;
        private String email;

        @Override
        public String toString() {
            return "KakaoAccount{" +
                    "profile=" + profile.toString() +
                    ", email='" + email + '\'' +
                    '}';
        }

        @Getter
        @AllArgsConstructor
        @Builder
        @ToString
        public static class Profile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private boolean is_default_image;
        }

    }

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "id=" + id +
                ", kakao_account=" + kakao_account.toString() +
                ", has_signed_up=" + has_signed_up +
                '}';
    }
}
