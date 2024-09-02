package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.service.CustomUserDetailsService;
import com.gymhub.gymhub.service.MemberService;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @Autowired
    private InMemoryRepository inMemoryRepository;

    @Operation(description = "This operation returns member information", tags = "Member Profile Page")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> getMember(@RequestParam String memberUsername) {
        try {
            MemberResponseDTO memberResponseDTO = (MemberResponseDTO) customUserDetailsService.loadUserByUsername(memberUsername);
            return ResponseEntity.ok(memberResponseDTO); // 200 OK
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation changes the information of a member", tags = "Member Profile Page")
    @PutMapping("/update/member-{id}")
    public ResponseEntity<Void> updateMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MemberRequestDTO memberRequestDTO) {
        try {
            memberService.updateMemberInfo(customUserDetails.getId(), memberRequestDTO);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation allows a member to follow another member", tags = "Member Actions")
    @PostMapping("/follow/{followingId}")
    public ResponseEntity<String> followMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long followingId) {
        try {
            if (customUserDetails == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            Long followerId = memberService.getMemberIdFromUserName(customUserDetails.getUsername());
            memberService.followMember(followerId, followingId);
            return ResponseEntity.ok("Member followed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to follow member due to server error.");
        }
    }

    @Operation(description = "This operation allows a member to unfollow another member", tags = "Member Actions")
    @PostMapping("/unfollow/{followingId}")
    public ResponseEntity<String> unfollowMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followingId) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            Long followerId = memberService.getMemberIdFromUserName(userDetails.getUsername());
            memberService.unfollowMember(followerId, followingId);
            return ResponseEntity.ok("Member unfollowed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unfollow member due to server error.");
        }
    }

    @Operation(description = "This operation returns the number of followers", tags = "Member Actions")
    @GetMapping("/followers/count")
    public ResponseEntity<Integer> getFollowersCount(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
            int followerCount = memberService.getFollowersNumber(memberId);
            return ResponseEntity.ok(followerCount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns the number of members the user is following", tags = "Member Actions")
    @GetMapping("/following/count")
    public ResponseEntity<Integer> getFollowingCount(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
            int followingCount = memberService.getFollowingNumber(memberId);
            return ResponseEntity.ok(followingCount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns the list of followers", tags = "Member Actions")
    @GetMapping("/followers")
    public ResponseEntity<Set<Long>> getFollowers(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
            Set<Long> followers = inMemoryRepository.getFollowersId(memberService.getMemberIdFromUserName(userDetails.getUsername()));
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns the list of members the user is following", tags = "Member Actions")
    @GetMapping("/following")
    public ResponseEntity<Set<Long>> getFollowing(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            Long memberId = memberService.getMemberIdFromUserName(userDetails.getUsername());
            Set<Long> following = memberService.getFollowingId(memberId);
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
}
