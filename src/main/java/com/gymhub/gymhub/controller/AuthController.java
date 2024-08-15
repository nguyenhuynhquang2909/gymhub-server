package com.gymhub.gymhub.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import com.gymhub.gymhub.dto.LoginRequestDTO;
import com.gymhub.gymhub.dto.RegisterRequestDTO;
import com.gymhub.gymhub.dto.TokenRefreshRequest;
import com.gymhub.gymhub.dto.TokenRefreshResponse;
import com.gymhub.gymhub.repository.UserRepository;
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
    private UserRepository userRepository;
    @Operation(
        summary = "Refresh Token", description = "Refresh the access token using a valid refresh token"
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
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.registerUser(registerRequestDTO);
    }

    @Operation(summary = "Login User", description = "Authenticate a user and returns a JWT Token")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthRespone> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.authenticateUser(loginRequestDTO);
    }

    @Operation(summary = "Get User Profile",
            description = "Get the profile of the authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        // Fetch the user from the database using the username from UserDetails
        String username = userDetails.getUsername();
        Member member = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a response with the user's information
        return ResponseEntity.ok(member);
    }
}
