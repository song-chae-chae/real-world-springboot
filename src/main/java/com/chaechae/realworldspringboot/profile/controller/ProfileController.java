package com.chaechae.realworldspringboot.profile.controller;

import com.chaechae.realworldspringboot.profile.request.FollowUser;
import com.chaechae.realworldspringboot.profile.response.ProfileResponse;
import com.chaechae.realworldspringboot.profile.service.ProfileService;
import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final TokenService tokenService;

    @PostMapping("/profiles/follow")
    public void follow(HttpServletResponse response, @RequestBody FollowUser request) {
        String authorization = response.getHeader("Authorization");
        Long uid = tokenService.getUid(authorization);

        profileService.follow(uid, request.getId());
    }

    @DeleteMapping("/profiles/{userId}/follow")
    public void unfollow(HttpServletResponse response, @PathVariable Long userId) {
        String authorization = response.getHeader("Authorization");
        Long uid = tokenService.getUid(authorization);
        profileService.unFollow(uid, userId);
    }

    @GetMapping("/profiles/{userId}")
    public ResponseEntity<ProfileResponse> get(@AuthenticationPrincipal UserLoginResponse authUser, @PathVariable("userId") Long userId) {
        Long authId = authUser.getId();
        ProfileResponse profileResponse = profileService.get(authId, userId);

        return ResponseEntity.status(200).body(profileResponse);
    }
}
