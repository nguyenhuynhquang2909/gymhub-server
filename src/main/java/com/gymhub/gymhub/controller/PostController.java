package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Post Request Handlers", description = "Handlers for Posts related requests")
@RestController
@RequestMapping("/post")
public class PostController {

    @Operation(
            description = "This operation returns a list of post belongs to the thread whose Id is included in the URL",
            tags = "Post Pages"
    )
    @GetMapping("/thread-{id}")
    public List<PostResponseDTO> getPosts(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page

            )
    {
        PostResponseDTO post1 = new PostResponseDTO();
        PostResponseDTO post2 = new PostResponseDTO();
        List<PostResponseDTO> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);
        return posts;
    }

    @Operation(
            description = "This operation returns a list of post belongs to the user whose Id is included in the URL",
            tags = "User Profile Page"
    )
    @GetMapping("/user-{id}")
    public List<PostResponseDTO> getUserPosts(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page)
    {
        PostResponseDTO post1 = new PostResponseDTO();
        PostResponseDTO post2 = new PostResponseDTO();
        List<PostResponseDTO> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);

        return posts;

    }

    @Operation(
            description = "This operation creates a new post",
            tags = "Post Page"
    )
    @PostMapping("/new")
    public ResponseEntity<Void> createPost(
            @RequestBody PostRequestDTO post)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "The operation increments or decrements the like count of a post",
            tags = "Post Container"
    )
    @PatchMapping("/like")
    public ResponseEntity<Void> changePostLike(
            @RequestBody IncreDecreDTO body)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation changes the content of a post",
            tags = "Post Container"
    )
    @PatchMapping("/update")
    public ResponseEntity<Void> updatePost(
            @RequestBody UpdateContentDTO body) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation reports a post to the server and sends a String as the reason",
            tags = "Post Container"
    )
    @PatchMapping("/report")
    public ResponseEntity<Void> reportThread(
            @RequestBody ReportRequestDTO reportRequestDTO
            ){
        return  new ResponseEntity<>(HttpStatus.OK);
    }



}
