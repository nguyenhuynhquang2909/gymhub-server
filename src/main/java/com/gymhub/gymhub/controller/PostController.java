package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.miscellaneous.IncreDecre;
import com.gymhub.gymhub.domain.miscellaneous.UpdateContent;
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
@RequestMapping("/api/post")
public class PostController {

    @Operation(
            description = "This operation returns a list of post belongs to the thread whose Id is included in the URL",
            tags = "Post Pages"
    )
    @GetMapping("/thread-{id}")
    public List<Post> getPosts(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page

            )
    {
       Post post1 = new Post();
       Post post2 = new Post();
       List<Post> posts = new ArrayList<>();
       posts.add(post1);
       posts.add(post2);
       return posts;
    }

    @Operation(
            description = "This operation returns a list of post belongs to the user whose Id is included in the URL",
            tags = "User Profile Page"
    )
    @GetMapping("/user-{id}")
    public List<Post> getUserPosts(
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page)
    {
        Post post1 = new Post();
        Post post2 = new Post();
        List<Post> posts = new ArrayList<>();

        return posts;

    }

    @Operation(
            description = "This operation creates a new post",
            tags = "Post Page"
    )
    @PostMapping("/new/user-{userId}/thread-{threadId}")
    public ResponseEntity<Void> createPost(
            @Parameter(description = "The id of the user this post belongs to", required = true)
            @PathVariable Long userId,
            @Parameter(description = "The id of the thread this post belongs to", required = true)
            @PathVariable Long threadId,
            @RequestBody Post post)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "The operation increments or decrements the like count of a post",
            tags = "Post Container"
    )
    @PatchMapping("/like/post-{postId}/user-{userId}")
    public ResponseEntity<Void> changePostLike(
            @Parameter(description = "The id of the post whose like count is to be changed", required = true)
            @PathVariable Long postId,
            @Parameter(description = "The user who likes or undoes their like", required = true)
            @PathVariable Long userId,
            @RequestBody IncreDecre body)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation changes the content of a post",
            tags = "Post Container"
    )
    @PatchMapping("/update/post-{id}")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "The id of the post whose content is to be changed", required = true)
            @PathVariable Long id,
            @RequestBody UpdateContent body) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation reports a post to the server and sends a String as the reason",
            tags = "Post Container"
    )
    @PatchMapping("/report/post-{id}")
    public ResponseEntity<Void> reportThread(
            @RequestBody String reason,
            @Parameter(description = "The id of the post to be reported", required = true)
            @PathVariable Long id){
        return  new ResponseEntity<>(HttpStatus.OK);
    }



}
