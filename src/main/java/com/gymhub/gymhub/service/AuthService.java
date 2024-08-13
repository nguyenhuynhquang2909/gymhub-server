package com.gymhub.gymhub.service;


import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.dto.AuthRespone;
import com.gymhub.gymhub.dto.LoginRequest;
import com.gymhub.gymhub.dto.RegisterRequest;

import java.sql.Date;
import java.util.Random;

@Service
public class AuthService {
    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberAccountRepository;

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        if (memberAccountRepository.checkIfMemberExistsByUserName(registerRequest.getUserName())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (memberAccountRepository.checkIfMemberExistsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        Random random = new Random();
        Long memberId = random.nextLong(50);
        Member member = new Member(memberId, registerRequest.getUserName(), passwordEncoder.encode(registerRequest.getPassword()), registerRequest.getEmail(), new Date(System.currentTimeMillis()));
        memberAccountRepository.save(member);
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
