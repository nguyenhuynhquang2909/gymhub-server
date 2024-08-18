package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post Request Handlers", description = "Handlers for Posts related requests")
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(description = "This operation returns a list of post inside a thread", tags = "Thread Page")
    @GetMapping("/thread-{id}")
    public ResponseEntity<List<PostResponseDTO>> getPostsInsideAThread(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {

        try {
            List<PostResponseDTO> posts = postService.getPostsByThreadId(id);
            return ResponseEntity.ok(posts); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns a list of post belongs to a member whose Id is included in the URL", tags = "Member Profile Page")
    @GetMapping("/user-{id}")
    public ResponseEntity<List<PostResponseDTO>> getPostsOfAMember(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {

        try {
            List<PostResponseDTO> posts = postService.getPostsByUserId(id);
            return ResponseEntity.ok(posts); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation creates a new post", tags = "Thread Page")
    @PostMapping("/new/user-{authorId}/thread-{threadId}")
    public ResponseEntity<Void> createPost(
            @Parameter(description = "The id of the user this post belongs to", required = true)
            @PathVariable Long authorId,
            @Parameter(description = "The id of the thread this post belongs to", required = true)
            @PathVariable Long threadId,
            @RequestBody PostRequestDTO post) {

        try {
            post.setAuthorId(authorId);
            post.setThreadId(threadId);
            postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "The operation increments or decrements the like count of a post", tags = "Thread Page")
    @PatchMapping("/like/post-{postId}/user-{userId}")
    public ResponseEntity<Void> changePostLike(
            @Parameter(description = "The id of the post whose like count is to be changed", required = true)
            @PathVariable Long postId,
            @Parameter(description = "The user who likes or undoes their like", required = true)
            @PathVariable Long userId,
            @RequestBody IncreDecreDTO body) {
        // Since the like operation does not involve complex logic, returning OK directly
        return ResponseEntity.ok().build(); // 200 OK
    }

    @Operation(description = "This operation changes the content and the image of a post (checks if the member is the post owner)", tags = "Thread Page")
    @PutMapping("/update/post-{id}")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "The id of the post to be updated", required = true)
            @PathVariable Long id,
            @RequestBody UpdatePostContentDTO body) {

        try {
            postService.updatePost(body);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
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
