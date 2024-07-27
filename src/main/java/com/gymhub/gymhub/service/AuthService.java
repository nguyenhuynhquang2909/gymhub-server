package com.gymhub.gymhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.domain.ForumAccount;
import com.gymhub.gymhub.dto.AuthRespone;
import com.gymhub.gymhub.dto.LoginRequest;
import com.gymhub.gymhub.dto.RegisterRequest;
import com.gymhub.gymhub.repository.ForumAccountRepository;

@Service
public class AuthService {
    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ForumAccountRepository forumAccountRepository;

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        if (forumAccountRepository.existsByUserName(registerRequest.getUserName())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (forumAccountRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        } 
        ForumAccount forumAccount = new ForumAccount(registerRequest.getUserName(), passwordEncoder.encode(registerRequest.getPassword()), registerRequest.getEmail());
        forumAccountRepository.save(forumAccount);
        return ResponseEntity.ok("User registered successfully");
    }
    public ResponseEntity<AuthRespone> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthRespone(jwt));
    }
}
