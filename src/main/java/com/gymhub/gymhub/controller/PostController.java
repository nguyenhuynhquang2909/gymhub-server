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
    PostService postService;

//

    @Operation(
            description = "This operation returns a list of post inside a thread",
            tags = "Thread Page"
    )
    @GetMapping("/thread-{id}")
    public List<PostResponseDTO> getPostsInsideAThread(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        // Call the service method to get the posts for the thread
        //fixed: has not show posts content inside a thread
        return postService.getPostsByThreadId(id);
    }

    @Operation(
            description = "This operation returns a list of post belongs to a member whose Id is included in the URL",
            tags = "Member Profile Page"
    )
    @GetMapping("/user-{id}")
    public List<PostResponseDTO> getPostsOfAMember(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page)
    {
        return postService.getPostsByUserId(id);

    }

    @Operation(
            description = "This operation creates a new post",
            tags = "Thread Page"
    )
    @PostMapping("/new/user-{userId}/thread-{threadId}")
    public ResponseEntity<Void> createPost(
            @Parameter(description = "The id of the user this post belongs to", required = true)
            @PathVariable Long authorId,
            @Parameter(description = "The id of the thread this post belongs to", required = true)
            @PathVariable Long threadId,
            @RequestBody PostRequestDTO post)
    {
        // Set the userId and threadId in the PostRequestDTO object
        post.setAuthorId(authorId);
        post.setThreadId(threadId);

        // Call the service method and return the response
        return postService.createPost(post);
    }


    @Operation(
            description = "The operation increments or decrements the like count of a post",
            tags = "Thread Page"
    )
    @PatchMapping("/like/post-{postId}/user-{userId}")
    public ResponseEntity<Void> changePostLike(
            @Parameter(description = "The id of the post whose like count is to be changed", required = true)
            @PathVariable Long postId,
            @Parameter(description = "The user who likes or undoes their like", required = true)
            @PathVariable Long userId,
            @RequestBody IncreDecreDTO body)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation changes the content and the image of a post (checks if the member is the post owner)",
            tags = "Thread Page"
    )
    @PutMapping("/update/post-{id}")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "The id of the post to be updated", required = true)
            @PathVariable Long id,
            @RequestBody UpdatePostContentDTO body) {

        // Call the service method to update the post content and image
        return postService.updatePost(body);
    }



    @Operation(
            description = "This operation reports a post to the server and returns a boolean indicating success",
            tags = "Thread Page"
    )
    @PatchMapping("/report")
    public ResponseEntity<String> reportPost(
            @RequestBody PostRequestDTO postRequestDTO,
            @RequestParam String reason)
    {
        // Call the service method to report the post with the provided PostRequestDTO and reason
        boolean success = postService.reportPost(postRequestDTO, reason);

        // Return appropriate response based on success or failure
        if (success) {
            return new ResponseEntity<>("Post reported successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to report post.", HttpStatus.BAD_REQUEST);
        }
    }





}
