package com.gymhub.gymhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.domain.ForumAccount;
import com.gymhub.gymhub.repository.ForumAccountRepository;

public class AuthService {
    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ForumAccountRepository forumAccountRepository;

   
}
