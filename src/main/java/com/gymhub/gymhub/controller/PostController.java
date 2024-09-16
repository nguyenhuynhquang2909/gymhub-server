package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.components.CookieManager;
import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.config.JwtTokenProvider;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.SessionStorage;
import com.gymhub.gymhub.service.CustomException.UnauthorizedUserException;
import com.gymhub.gymhub.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @PathVariable Long id) {

        try {
            Cookie[] cookies = request.getCookies();
            cookieManager.setCookies(cookies);
            // Check if the user is logged in by getting the JWT token from the request
            String token = cookieManager.getCookieValue("AuthenticationToken");
            UUID sessionID;

            if (token == null) { // User is not logged in
                if (cookieManager.getCookieValue("SessionID") == null) { // No existing session ID
                    sessionID = sessionStorage.createNewSessionWhenViewThread();
                    Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
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
            }
            else { // User is logged in
                Long userID = jwtTokenProvider.getClaimsFromJwt(token).get("userID", Long.class);
                if (cookieManager.getCookieValue("SessionID") == null) { // No session ID in cookie
                    sessionID = sessionStorage.createNewSession(userID);
                    Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
                    sessionCookie.setHttpOnly(true);
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
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null or handle as per your application's needs
        }
    }

//    @Operation(description = "This operation returns a list of posts belonging to the authenticated member", tags = "Member Profile Page")
//    @GetMapping("/user")
//    public List<PostResponseDTO> getPostsOfAMember(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @Parameter(description = "The number of posts to be returned in a single fetch", required = false)
//            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
//            @Parameter(description = "The next page to be fetched", required = false)
//            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
//
//        try {
//            return postService.getPostsByUserId(userDetails.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null; // Return null or handle as per your application's needs
//        }
//    }

    @Operation(description = "This operation creates a new post", tags = "Thread Page")
    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ToxicStatusEnum> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the thread this post belongs to", required = true)
            @ModelAttribute PostRequestDTO post,
            @RequestParam(value = "uploadedFile", required = false) List<MultipartFile> files) {
        try {
            ToxicStatusEnum toxicity = postService.createPost(post, files, userDetails);
            return new ResponseEntity<>(toxicity, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "This operation changes the content and the image of a post (checks if the member is the post owner)", tags = "Thread Page")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ToxicStatusEnum> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the post to be updated", required = true)
            @ModelAttribute UpdatePostRequestDTO body,
            @RequestParam(value = "uploadedFile", required = false) List<MultipartFile> files) {

        try {
            ToxicStatusEnum statusEnum = postService.updatePost(userDetails.getId(), body, files);
            return new ResponseEntity<>(statusEnum, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof UnauthorizedUserException){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "This operation reports a post to the server and returns a boolean indicating success", tags = "Thread Page")
    @PutMapping("/report/post-{id}/{threadId}")
    public ResponseEntity<String> reportPost(
            @PathVariable Long id,
            @PathVariable Long threadId,
            @RequestParam String reason) {

        try {
            boolean success = postService.reportPost(threadId, reason, id);
            if (success) {
                return ResponseEntity.ok("Post reported successfully."); // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report post."); // 400 Bad Request
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to report post due to server error."); // 500 Internal Server Error
        }
    }


    @Operation(description = "This operation likes a post by a member", tags = "Thread Page")
    @PutMapping("/like/post-{id}")
    public ResponseEntity<Void> likePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        try {
            // Create a MemberRequestDTO from the authenticated user's details
            MemberRequestDTO memberRequestDTO = new MemberRequestDTO(userDetails.getId());

            // Call the likePost method in the service
            boolean success = postService.likePost(memberRequestDTO, id, userDetails.getId());

            if (success) {
                return ResponseEntity.status(HttpStatus.OK).build(); // 200 OK if the post was liked successfully
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request if the post was already liked
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error if something went wrong
        }
    }

    @Operation(description = "This operation unlikes a post by a member", tags = "Thread Page")
    @PutMapping("/unlike/post-{id}")
    public ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        try {
            // Create a MemberRequestDTO from the authenticated user's details
            MemberRequestDTO memberRequestDTO = new MemberRequestDTO(userDetails.getId());

            // Call the unlikePost method in the service
            boolean success = postService.unlikePost(memberRequestDTO, id, userDetails.getId());

            if (success) {
                return ResponseEntity.status(HttpStatus.OK).build(); // 200 OK if the post was unliked successfully
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request if the post was not liked before
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error if something went wrong
        }
    }
}
