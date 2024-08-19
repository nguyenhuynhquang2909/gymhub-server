package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.components.CookieManager;
import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.SessionStorage;
import com.gymhub.gymhub.service.PostService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Post Request Handlers", description = "Handlers for Posts related requests")
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private SessionStorage sessionStorage;

    @Autowired
    private CookieManager cookieManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Operation(description = "This operation returns a list of posts inside a thread", tags = "Thread Page")
    @GetMapping("/thread-{id}")
    public List<PostResponseDTO> getPostsInsideAThread(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader("Cookie") String cookies,
            @PathVariable Long id,
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {

        // Check if the user is logged in by getting the JWT token from the request
        String token = cookieManager.getCookieValue("AuthenticationToken");
        UUID sessionID;

        if (token == null) { // User is not logged in
            if (cookieManager.getCookieValue("SessionID") == null) { // No existing session ID
                sessionID = sessionStorage.createNewSessionWhenViewThread();
                Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
                sessionCookie.setHttpOnly(true);
                sessionCookie.setSecure(true);
                sessionCookie.setPath("/");
                sessionCookie.setMaxAge(60 * 60);
                response.addCookie(sessionCookie);
                sessionStorage.addThreadToThreadView(sessionID, id);
            } else { // Existing session
                sessionID = UUID.fromString(cookieManager.getCookieValue("SessionID"));
                sessionStorage.addThreadToThreadView(sessionID, id);
            }
            return postService.getPostsByThreadId(id);
        } else { // User is logged in
            Long userID = jwtTokenProvider.getClaimsFromJwt(token).get("userID", Long.class);
            if (cookieManager.getCookieValue("SessionID") == null) { // No session ID in cookie
                sessionID = sessionStorage.createNewSession(userID);
                Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
                sessionCookie.setHttpOnly(true);
                sessionCookie.setSecure(true);
                sessionCookie.setPath("/");
                sessionCookie.setMaxAge(60 * 60);
                response.addCookie(sessionCookie);
                sessionStorage.addThreadToThreadView(sessionID, id);
            } else {
                sessionID = UUID.fromString(cookieManager.getCookieValue("SessionID"));
                sessionStorage.addThreadToThreadView(sessionID, id);
            }
            return postService.getPostsByThreadId(id);
        }
    }

    @Operation(description = "This operation returns a list of posts belonging to the authenticated member", tags = "Member Profile Page")
    @GetMapping("/user")
    public List<PostResponseDTO> getPostsOfAMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The number of posts to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {

        return postService.getPostsByUserId(userDetails.getId());
    }

    @Operation(description = "This operation creates a new post", tags = "Thread Page")
    @PostMapping("/new/thread-{threadId}")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the thread this post belongs to", required = true)
            @PathVariable Long threadId,
            @RequestBody PostRequestDTO post) {



        boolean success = postService.createPost(userDetails.getId(),post);
        //API AI

        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created if the post was created successfully
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request if there was an error
        }
    }


    @Operation(description = "This operation increments or decrements the like count of a post", tags = "Thread Page")
    @PatchMapping("/like/post-{postId}")
    public ResponseEntity<Void> changePostLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the post whose like count is to be changed", required = true)
            @PathVariable Long postId,
            @RequestBody IncreDecreDTO body) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(description = "This operation changes the content and the image of a post (checks if the member is the post owner)", tags = "Thread Page")
    @PatchMapping("/update/post-{id}")
    public ResponseEntity<Void> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the post to be updated", required = true)
            @PathVariable Long id,
            @RequestBody UpdatePostContentDTO body) {

        boolean success = postService.updatePost(userDetails.getId(), body);

        if (success) {
            return ResponseEntity.ok().build(); // 200 OK if the update was successful
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden if the user is not authorized or any other error
        }
    }


    @Operation(description = "This operation reports a post to the server and returns a boolean indicating success", tags = "Thread Page")
    @PatchMapping("/report")
    public ResponseEntity<String> reportPost(
            @RequestBody PostRequestDTO postRequestDTO,
            @RequestParam String reason) {

        try {
            boolean success = postService.reportPost(postRequestDTO, reason);
            if (success) {
                return ResponseEntity.ok("Post reported successfully."); // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report post."); // 400 Bad Request
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to report post due to server error."); // 500 Internal Server Error
        }
    }
}
