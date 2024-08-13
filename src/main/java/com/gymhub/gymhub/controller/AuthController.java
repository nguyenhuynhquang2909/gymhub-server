package com.gymhub.gymhub.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.RefreshToken;
import com.gymhub.gymhub.dto.AuthRespone;
import com.gymhub.gymhub.dto.LoginRequest;
import com.gymhub.gymhub.dto.RegisterRequest;
import com.gymhub.gymhub.dto.TokenRefreshRequest;
import com.gymhub.gymhub.dto.TokenRefreshResponse;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.service.AuthService;
import com.gymhub.gymhub.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name =  "Authentication Request Handlers", description="Handlers for Authentication related requests")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;
    @Operation(
        summary = "Refresh Token", description = "Refresh the access token using a valid refresh token"
    )
    @ApiResponses( value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode =  "400", description = "Invalid refresh token"),
        @ApiResponse(responseCode =  "404", description = "Refresh token not found")
    }
    )
    
    @PostMapping(value = "/refresh-token", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String token = jwtTokenProvider.generateTokenFromUsername(user.getUserName());
                return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
            })
            .orElseThrow(() -> new RuntimeException("Refresh token is not in the database"));
    }
    @Operation(summary = "Register User", description = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration details")
    })
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @Operation(summary = "Login User", description = "Authenticate a user and returns a JWT Token")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthRespone> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @Operation(summary = "Get User Profile", description = "Get the profile of the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile fetched successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        // Fetch the user from the database using the username from UserDetails
        String username = userDetails.getUsername();
        Member member = memberRepository.findMemberByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a response with the user's information
        return ResponseEntity.ok(member);
    }
}
