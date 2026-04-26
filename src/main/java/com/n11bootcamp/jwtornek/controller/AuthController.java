package com.n11bootcamp.jwtornek.controller;

import com.n11bootcamp.jwtornek.auth.TokenManager;
import com.n11bootcamp.jwtornek.request.LoginRequest;
import com.n11bootcamp.jwtornek.request.RefreshTokenRequest;
import com.n11bootcamp.jwtornek.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        return ResponseEntity.ok(new TokenResponse(
                tokenManager.generateAccessToken(loginRequest.getUsername()),
                tokenManager.generateRefreshToken(loginRequest.getUsername())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String accessToken = tokenManager.refreshAccessToken(refreshTokenRequest.getRefreshToken());

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshTokenRequest.getRefreshToken()));
    }
}
