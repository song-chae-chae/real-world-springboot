package com.chaechae.realworldspringboot.user.controller;

import com.chaechae.realworldspringboot.security.jwt.service.TokenService;
import com.chaechae.realworldspringboot.user.request.SocialLoginRequest;
import com.chaechae.realworldspringboot.user.request.UpdateUserRequest;
import com.chaechae.realworldspringboot.user.response.UserLoginResponse;
import com.chaechae.realworldspringboot.user.response.UserResponse;
import com.chaechae.realworldspringboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/users/login")
    public ResponseEntity<UserLoginResponse> doLogin(@RequestBody SocialLoginRequest request) {
        UserLoginResponse userLoginResponse = userService.doSocialLogin(request.getCode());
        return ResponseEntity.status(200).body(userLoginResponse);
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> currentUser(HttpServletResponse res) {
        String authorization = res.getHeader("Authorization");
        Long uid = tokenService.getUid(authorization);
        UserResponse userResponse = userService.get(uid);

        return ResponseEntity.status(200).body(userResponse);
    }

    @PutMapping("/user")
    public void updateUser(@RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(request);
    }

    @GetMapping("/user/test")
    public String jwtTest() {
        return "hi";
    }

    @GetMapping("/user/security-test")
    public String jwtTest2() {
        return "security-test";
    }
}
