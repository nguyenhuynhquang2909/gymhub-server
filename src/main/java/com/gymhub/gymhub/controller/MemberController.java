package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.service.CustomUserDetailsService;
import com.gymhub.gymhub.service.MemberService;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@Tag(name = "Member Request Handler", description = "Handlers for members related requests")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Operation(description = "This operation returns member information",
            tags = "Member Profile Page")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable Long id) {
        try {
            MemberResponseDTO memberResponseDTO = (MemberResponseDTO) customUserDetailsService.loadUserByUsername(id.toString());
            return ResponseEntity.ok(memberResponseDTO); // 200 OK
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @Operation(description = "This operation changes the information of a member",
            tags = "Member Profile Page")
    @PutMapping("/update/member-{id}")
    public ResponseEntity<Void> updateMember(@RequestBody MemberRequestDTO memberRequestDTO) {
        try {
            memberService.updateMemberInfo(memberRequestDTO);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    //API: Follow a Member
    @Operation(description = "This operation allows a member to follow another member",
            tags = "Member Actions")
    @PostMapping("/follow/{followerId}/following/{followingId}")
    public ResponseEntity<Void> followMember(@PathVariable Long followerId, @PathVariable Long followingId) {
        memberService.followAnotherMember(followerId, followingId);
        return ResponseEntity.ok().build(); // 200 OK
    }

    //API: Unfollow a Member
    @Operation(description = "This operation allows a member to unfollow another member",
            tags = "Member Actions")
    @PostMapping("/unfollow/{followerId}/following/{followingId}")
    public ResponseEntity<Void> unfollowMember(@PathVariable Long followerId, @PathVariable Long followingId) {
        memberService.unfollowAnotherMember(followerId, followingId);
        return ResponseEntity.ok().build(); // 200 OK
    }


}

// Additional controller methods handling HTTP status codes

//Notifications
//API : show all notifications => notificationDTO
    //your post has been flagged as ..
    //your thread has been flagged
    //your have been banned until
    //your post has a new like
    //your thread has a new post
    //you have a new follower
    //you have a new private conversation
    //a following member has created thread
    //a following member has created a posts

    //








