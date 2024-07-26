package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.domain.miscellaneous.IncreDecre;
import com.gymhub.gymhub.domain.miscellaneous.ListOfPostsPaginationJson;
import com.gymhub.gymhub.domain.miscellaneous.UpdateContent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "Handlers for Posts related requests")
@RestController
@RequestMapping("/api/post")
public class PostController {

    @ApiOperation(
            value = "This operation returns a list of post belongs to the thread whose Id is included in the URL",
            tags = "Post Pages"
    )
    @GetMapping("/thread-{id}")
    public ListOfPostsPaginationJson getPosts(
            @PathVariable Long id,
            @ApiParam(value = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "id of the next thread id from which the server will fetch", required = false)
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
            )
    {
       Post post1 = new Post();
       Post post2 = new Post();
       List<Post> posts = new ArrayList<>();
       ListOfPostsPaginationJson json = new ListOfPostsPaginationJson();
       json.setPosts(posts);
       json.setAfter(after);
       return json;
    }

    @ApiOperation(
            value = "This operation returns a list of post belongs to the user whose Id is included in the URL",
            tags = "User Profile Page"
    )
    @GetMapping("/user-{id}")
    public ListOfPostsPaginationJson getUserPosts(
            @PathVariable Long id,
            @ApiParam(value = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @ApiParam(value = "id of the next thread id from which the server will fetch", required = false)
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after)
    {
        Post post1 = new Post();
        Post post2 = new Post();
        List<Post> posts = new ArrayList<>();
        ListOfPostsPaginationJson json = new ListOfPostsPaginationJson();
        json.setPosts(posts);
        json.setAfter(after);
        return json;

    }

    @ApiOperation(
            value = "This operation creates a new post",
            tags = "Post Page"
    )
    @PostMapping("/new/user-{userId}/thread-{threadId}")
    public ResponseEntity<Void> createPost(
            @ApiParam(value = "The id of the user this post belongs to", required = true)
            @PathVariable Long userId,
            @ApiParam(value = "The id of the thread this post belongs to", required = true)
            @PathVariable Long threadId,
            @RequestBody Post post)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(
            value = "The operation increments or decrements the like count of a post",
            tags = "Post Container"
    )
    @PatchMapping("/like/post-{postId}/user-{userId}")
    public ResponseEntity<Void> changePostLike(
            @ApiParam(value = "The id of the post whose like count is to be changed", required = true)
            @PathVariable Long postId,
            @ApiParam(value = "The user who likes or undoes their like", required = true)
            @PathVariable Long userId,
            @RequestBody IncreDecre body)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(
            value = "This operation changes the content of a post",
            tags = "Post Container"
    )
    @PatchMapping("/update/post-{id}")
    public ResponseEntity<Void> updatePost(
            @ApiParam(value = "The id of the post whose content is to be changed", required = true)
            @PathVariable Long id,
            @RequestBody UpdateContent body) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(
            value = "This operation reports a post to the server and sends a String as the reason",
            tags = "Post Container"
    )
    @PatchMapping("/report/post-{id}")
    public ResponseEntity<Void> reportThread(
            @RequestBody String reason,
            @ApiParam(value = "The id of the post to be reported", required = true)
            @PathVariable Long id){
        return  new ResponseEntity<>(HttpStatus.OK);
    }



}
