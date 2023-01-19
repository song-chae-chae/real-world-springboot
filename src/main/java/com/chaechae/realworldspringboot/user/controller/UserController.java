package com.chaechae.realworldspringboot.user.controller;

import com.chaechae.realworldspringboot.user.request.SocialLoginRequest;
import com.chaechae.realworldspringboot.user.request.UpdateUserRequest;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import com.chaechae.realworldspringboot.user.response.UserResponse;
import com.chaechae.realworldspringboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/users/login")
    public ResponseEntity<UserLoginResponse> doLogin(@RequestBody SocialLoginRequest request) {
        UserLoginResponse userLoginResponse = userService.doSocialLogin(request.getCode());
        return ResponseEntity.status(200).body(userLoginResponse);
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> currentUser(@AuthenticationPrincipal UserLoginResponse authUser) {
        UserResponse userResponse = new UserResponse(userService.get(authUser.getId()));

        return ResponseEntity.status(200).body(userResponse);
    }

    @PutMapping("/user")
    public void updateUser(@AuthenticationPrincipal UserLoginResponse authUser, @RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(authUser, request);
    }
}
