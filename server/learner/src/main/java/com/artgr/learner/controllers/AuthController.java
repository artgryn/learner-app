package com.artgr.learner.controllers;

import com.artgr.learner.data.presentation.LoginRequest;
import com.artgr.learner.data.presentation.RefreshRequest;
import com.artgr.learner.data.presentation.RegisterRequest;
import com.artgr.learner.data.presentation.TokenPair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<TokenPair> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dummyTokenPair());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(dummyTokenPair());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(dummyTokenPair());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    private TokenPair dummyTokenPair() {
        return new TokenPair("dummy-access-token", "dummy-refresh-token", 1800);
    }
}
