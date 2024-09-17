package com.gymhub.gymhub.controller;


import com.gymhub.gymhub.components.CookieManager;
import com.gymhub.gymhub.in_memory.SessionStorage;
import com.gymhub.gymhub.repository.MemberRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.gymhub.gymhub.service.AuthService;
import com.gymhub.gymhub.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

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

    @Autowired
    private CookieManager cookieManager;

    @Autowired
    private SessionStorage sessionStorage;

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

    @Operation(summary = "Register Member User", description = "Register a new user as member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration details")
    })
    @PostMapping(value = "/register/member", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerMember(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.registerMember(registerRequestDTO);
    }

    @Operation(summary = "Register Mod User", description = "Register a new user as moderator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration details")
    })
    @PostMapping(value = "/register/mod", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerMod(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.registerMod(registerRequestDTO);
    }



    @Operation(summary = "Login User", description = "Authenticate a user and returns a JWT Token")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthRespone> authenticateUser(
            @RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletResponse response) {
        return authService.authenticateUser(response, loginRequestDTO);

    }

    @Operation(summary = "Log users out", description = "Logs out the user by clearing the authentication token and creating a new guest session")
    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logUserOut(HttpServletResponse response) {
        // Clear the authentication token stored in cookies
        Cookie authCookie = new Cookie("AuthenticationCookie", null);
        authCookie.setPath("/"); // Make sure this matches the path of the original cookie
        authCookie.setHttpOnly(true);
        authCookie.setMaxAge(0); // Immediately expire the cookie
        response.addCookie(authCookie);

        // Clear security context to log the user out
        SecurityContextHolder.clearContext();

        // Generate a new session ID for a guest user
        UUID sessionID = sessionStorage.createNewSessionWhenViewThread();
        Cookie sessionCookie = new Cookie("SessionCookie", sessionID.toString());
        sessionCookie.setPath("/"); // Ensure the path is correct for the session cookie
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(60 * 60); // Set session cookie expiration (1 hour)
        response.addCookie(sessionCookie);

        return new ResponseEntity<>(HttpStatus.OK); // Return HTTP 200 OK status
    }


    @Operation(
            summary = "Get User Profile",
            description = "Get the profile of the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile fetched successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity<?> getUserProfile(

    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        // Fetch the user from the database using the username from UserDetails
        String username = authentication.getName();
        Member member = memberRepository.findMemberByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a response with the user's information
        return ResponseEntity.ok(member);
    }
}
