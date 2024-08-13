package com.gymhub.gymhub.service;


import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.dto.AuthRespone;
import com.gymhub.gymhub.dto.LoginRequestDTO;
import com.gymhub.gymhub.dto.RegisterRequestDTO;

import java.sql.Date;

@Service
public class AuthService {
    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository memberAccountRepository;


    public ResponseEntity<?> registerUser(RegisterRequestDTO registerRequestDTO) {
        if (memberAccountRepository.existsByUserName(registerRequestDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (memberAccountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());
        Member member = new Member(registerRequestDTO.getUsername(), encodedPassword, registerRequestDTO.getEmail(), new Date(System.currentTimeMillis()));
        System.out.println(registerRequestDTO.getUsername());

        memberAccountRepository.save(member);
        return ResponseEntity.ok("User registered successfully");
    }
    public ResponseEntity<AuthRespone> authenticateUser(LoginRequestDTO loginRequestDTO) {
        System.out.println("Username: " + memberAccountRepository.findByUserName(loginRequestDTO.getUsername()));
        System.out.println("ID: " + memberAccountRepository.findById(12L));
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthRespone(jwt));
    }
}
