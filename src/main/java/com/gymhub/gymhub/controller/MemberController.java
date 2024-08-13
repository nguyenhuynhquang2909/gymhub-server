package com.gymhub.gymhub.controller;


import com.gymhub.gymhub.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/follow/{followingId}")
    public ResponseEntity<String> followMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followingId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Long followerId = memberService.getMemberIdFromUserName(userDetails.getUsername());
        memberService.followMember(followerId, followingId);
        return ResponseEntity.ok("Member followed successfully");
    }

    @PostMapping("/unfollow/{followingId}")
    public ResponseEntity<String> unfollowMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followingId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Long followerId = memberService.getMemberIdFromUserName(userDetails.getUsername());
        memberService.unfollowMember(followerId, followingId);
        return ResponseEntity.ok("Member unfollowed successfully");
    }

    @GetMapping("/followers/count")
    public ResponseEntity<Integer> getFollowersCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
        int followerCount = memberService.getFollowersNumber(memberId);
        return ResponseEntity.ok(followerCount);
    }

    @GetMapping("/following/count")
    public ResponseEntity<Integer> getFollowingCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
        int followingCount = memberService.getFollowingNumber(memberId);
        return ResponseEntity.ok(followingCount);
    }

    @GetMapping("/followers")
    public ResponseEntity<Set<Long>> getFollowers(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
        Set<Long> followers = memberService.getFollowers(memberId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following")
    public ResponseEntity<Set<Long>> getFollowing(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
        Set<Long> following = memberService.getFollowing(memberId);
        return ResponseEntity.ok(following);
    }
}
