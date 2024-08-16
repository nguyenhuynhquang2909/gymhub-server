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
    PostService postService;
@Autowired
    SessionStorage sessionStorage;
@Autowired
    CookieManager cookieManager;
@Autowired
    JwtTokenProvider jwtTokenProvider;

//

    @Operation(
            description = "This operation returns a list of post inside a thread",
            tags = "Thread Page"
    )
    @GetMapping("/thread{id}")
    public List<PostResponseDTO> getPostsInsideAThread(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader("Cookie") String cookies,
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        // Call the service method to get the posts for the thread
        //fixed: has not show posts content inside a thread

        //Check if the user is logged in by getting the jwt token from the request
        String token = cookieManager.getCookieValue("AuthenticationToken");
        UUID sessionID;
        if (token == null) {
            //if there is no token (meaning the user is not logged in) check if there is an existing session id
            if (cookieManager.getCookieValue("SessionID") == null){
                //If there is no existing session ID, create a new session
                sessionID = sessionStorage.createNewSessionWhenViewThread();
                //Save the sessionID in cookie
                Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
                sessionCookie.setHttpOnly(true); // Makes the cookie accessible only through HTTP (not JavaScript)
                sessionCookie.setSecure(true); // Ensures the cookie is sent over HTTPS only
                sessionCookie.setPath("/"); // Sets the cookie's path to the root of the application
                sessionCookie.setMaxAge(60 * 60); // Sets the cookie to expire after 1 hour (in seconds)
                response.addCookie(sessionCookie);
                //put the thread ID and its view time into the session
                sessionStorage.addThreadToThreadView(sessionID, id);
            }
            else {
                //If there is an existing session
                sessionID = UUID.fromString(cookieManager.getCookieValue("SessionID"));
                sessionStorage.addThreadToThreadView(sessionID, id);
            }
            return postService.getPostsByThreadId(id, null);
        }
        else {
            //If the user is logged in but there is no session Id in the cookie (the old session has expired
            Long userID = jwtTokenProvider.getClaimsFromJwt(token).get("userID", Long.class);
            if (cookieManager.getCookieValue("SessionID") == null){
                //Retrieve the user id from token
                Claims claims = jwtTokenProvider.getClaimsFromJwt(token);
                sessionID = sessionStorage.createNewSession(userID);
                Cookie sessionCookie = new Cookie("SessionID", sessionID.toString());
                sessionCookie.setHttpOnly(true); // Makes the cookie accessible only through HTTP (not JavaScript)
                sessionCookie.setSecure(true); // Ensures the cookie is sent over HTTPS only
                sessionCookie.setPath("/"); // Sets the cookie's path to the root of the application
                sessionCookie.setMaxAge(60 * 60); // Sets the cookie to expire after 1 hour (in seconds)
                response.addCookie(sessionCookie);
                //put the thread ID and its view time into the session
                sessionStorage.addThreadToThreadView(sessionID, id);


            }
            else {
                sessionID = UUID.fromString(cookieManager.getCookieValue("SessionID"));
                sessionStorage.addThreadToThreadView(sessionID, id);

            }
            return postService.getPostsByThreadId(id, userID);
        }


    }









    @Operation(
            description = "This operation returns a list of post belongs to a member whose Id is included in the URL",
            tags = "Member Profile Page"
    )
    @GetMapping("/user")
    public List<PostResponseDTO> getPostsOfAMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page)
    {
        return postService.getPostsByUserId(userDetails.getId());

    }

    @Operation(
            description = "This operation creates a new post",
            tags = "Thread Page"
    )
    @PostMapping("/new/thread{threadId}")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the thread this post belongs to", required = true)
            @PathVariable Long threadId,
            @RequestBody PostRequestDTO post)
    {
        return postService.createPost(userDetails.getId(), threadId, post);
    }


    @Operation(
            description = "The operation increments or decrements the like count of a post",
            tags = "Thread Page"
    )
    @PatchMapping("/like/{postId}")
    public ResponseEntity<Void> changePostLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the post whose like count is to be changed", required = true)
            @PathVariable Long postId,
            @RequestBody IncreDecreDTO body)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation changes the content and the image of a post (checks if the member is the post owner)",
            tags = "Thread Page"
    )
    @PatchMapping("/update/{id}")
    public ResponseEntity<Void> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The id of the post to be updated", required = true)
            @PathVariable Long id,
            @RequestBody UpdatePostContentDTO body) {

        // Call the service method to update the post content and image
        return postService.updatePost(userDetails.getId(), body);
    }



    @Operation(
            description = "This operation reports a post to the server and returns a boolean indicating success",
            tags = "Thread Page"
    )
    @PatchMapping("/report")
    public ResponseEntity<String> reportPost(
            @RequestBody ReportPostRequestDTO reportPostRequestDTO)
             {
        // Set the post ID in the DTO
        reportPostRequestDTO.setId(reportPostRequestDTO.getId());

        // Assuming you have a way to get the threadId for the post, pass it to the service method
        long threadId = reportPostRequestDTO.getThreadId(); // Example method to get threadId

        // Call the service method to report the post
        boolean success = postService.reportPost(reportPostRequestDTO, threadId);

        // Return appropriate response based on success or failure
        if (success) {
            return new ResponseEntity<>("Post reported successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to report post.", HttpStatus.BAD_REQUEST);
        }
    }



}
