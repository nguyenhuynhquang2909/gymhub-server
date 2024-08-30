package com.gymhub.gymhub.service;


import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.helper.MemberSequence;
import com.gymhub.gymhub.in_memory.SessionStorage;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private MemberSequence memberSequence;

    @Autowired
    private InMemoryRepository inMemoryRepository;
    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberAccountRepository;

    @Autowired
    SessionStorage sessionStorage;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public ResponseEntity<?> registerUser(RegisterRequestDTO registerRequestDTO) {
        if (memberAccountRepository.existsByUserName(registerRequestDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (memberAccountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());
        Long memberId = memberSequence.getUserId();
        Member member = new Member(memberId, registerRequestDTO.getUsername(), encodedPassword, registerRequestDTO.getEmail(), new Date(System.currentTimeMillis()));
        System.out.println(registerRequestDTO.getUsername());
        memberAccountRepository.save(member);
        inMemoryRepository.addUserToCache(memberId);
        return ResponseEntity.ok("User registered successfully");
    }
    public ResponseEntity<AuthRespone> authenticateUser(HttpServletResponse response, LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);
        //Save Token in the cookie
        Cookie cookie = new Cookie("AuthenticationToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtExpiration / 1000);
        response.addCookie(cookie);
        //Create a new session
        Long userId = tokenProvider.getClaimsFromJwt(jwt).get("userId", Long.class);
        UUID sessionID = sessionStorage.createNewSession(userId);
        System.out.println("Session ID: " + sessionID);
        Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(60 * 60);
        response.addCookie(sessionCookie);


        return ResponseEntity.ok(new AuthRespone(jwt));
    }
}
