package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.mapper.ThreadMapper;
import com.gymhub.gymhub.repository.PostRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.service.CustomUserDetailsService;
import com.gymhub.gymhub.service.ModService;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mod")
@Tag(name = "Mod Request Handler", description = "Handlers for mod's related requests")
public class ModController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModService modService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Operation(description = "This operation returns mod profile information",
            tags = "Mod Profile Page")
    @GetMapping("/{id}")
    public ResponseEntity<ModeratorRequestAndResponseDTO> getMod(@RequestBody ModeratorRequestAndResponseDTO modDTO) {
        try {
            String modUsername = modDTO.getUsername();
            ModeratorRequestAndResponseDTO response = (ModeratorRequestAndResponseDTO) customUserDetailsService.loadUserByUsername(modUsername);
            return ResponseEntity.ok(response); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @Operation(description = "This operation updates mod profile information",
            tags = "Mod Profile Page")
    @PutMapping("/update/mod-{id}")
    public ResponseEntity<Void> updateMod(@RequestBody ModeratorRequestAndResponseDTO modDTO) {
        try {
            modService.updateModInfo(modDTO);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        }
    }

    @Operation(description = "This operation fills the mod dashboard with 3 tables: pending posts, pending threads, and banned members",
            tags = "Mod Dashboard Page")
    @GetMapping("/dashboard/mod-{id}")
    public ResponseEntity<ModDashboardTablesResponseDTO> fillModDashboard() {
        try {
            // Get the list of PendingPostDTO and PendingThreadDTO directly from the service
            List<PendingPostDTO> pendingPosts = modService.getAllPendingPosts();
            List<PendingThreadDTO> pendingThreads = modService.getAllPendingThreads();

            // Retrieve banned members
            List<BannedMemberDTO> bannedMembers = modService.displayBannedMembers(modService.getBannedMembers());

            // Create the response object
            ModDashboardTablesResponseDTO response = new ModDashboardTablesResponseDTO(pendingPosts, pendingThreads, bannedMembers);
            return ResponseEntity.ok(response); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }



    @Operation(description = "This operation helps mod to decide whether a pending post is toxic or not",
            tags = "Mod Dashboard Page")
    @PatchMapping("/mod-{modId}/resolvePendingPost-{postId}")
    public ResponseEntity<Void> resolveAPendingPost(
            @PathVariable("modId") Long modId,
            @PathVariable("postId") Long postId,
            @RequestParam("threadId") Long threadId,
            @RequestParam("newToxicStatus") ToxicStatusEnum newToxicStatus,
            @RequestParam("reason") String reason) {
        try {
            modService.resolveAPendingPost(postId, threadId, newToxicStatus, reason);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation helps mod to decide whether a pending thread is toxic or not",
            tags = "Mod Dashboard Page")
    @PatchMapping("/mod-{modId}/resolvePendingThread-{threadId}")
    public ResponseEntity<Void> resolveAPendingThread(
            @PathVariable("modId") Long modId,
            @PathVariable("threadId") Long threadId,
            @RequestParam("category") ThreadCategoryEnum category,
            @RequestParam("newToxicStatus") ToxicStatusEnum newToxicStatus,
            @RequestParam("reason") String reason) {
        try {
            modService.resolveAPendingThread(threadId, category, newToxicStatus, reason);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation helps mod to ban a member with a duration in milliseconds", tags = "Mod Dashboard Page")
    @PatchMapping("/dashboard/mod-{modId}/ban/member-{memberId}")
    public ResponseEntity<Void> banAMember(@PathVariable("memberId") Long userId,
                                           @RequestParam("duration") Long durationMillis,
                                           @RequestParam("reason") String reason) {
        try {
            Date banUntilDate = new Date(System.currentTimeMillis() + durationMillis);
            modService.banMember(userId, banUntilDate, reason);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation helps mod to remove a member from a ban list", tags = "Mod Dashboard Page")
    @DeleteMapping("/dashboard/mod-{modId}/unBan/member-{memberId}")
    public ResponseEntity<Void> unBanAMember(@PathVariable("memberId") Long userId) {
        try {
            modService.unbanMember(userId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation helps mod to immediately ban a post while surfing the forum", tags = "Mod Surfing Forum")
    @PatchMapping("/dashboard/mod-{modId}/ban/post-{postId}/thread-{threadId}/status-{status}")
    public ResponseEntity<Void> banAPost(
            @PathVariable("modId") Long modId,
            @PathVariable("postId") Long postId,
            @PathVariable("threadId") Long threadId,
            @PathVariable("status") ToxicStatusEnum newToxicStatus,
            @RequestParam("reason") String reason) {
        try {
            modService.banAPostWhileSurfing(postId, threadId, newToxicStatus, reason);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation helps mod to immediately ban a thread while surfing the forum", tags = "Mod Surfing Forum")
    @PatchMapping("/dashboard/mod-{modId}/ban/thread-{threadId}/category-{category}/status-{status}")
    public ResponseEntity<Void> banAThread(
            @PathVariable("modId") Long modId,
            @PathVariable("threadId") Long threadId,
            @PathVariable("category") ThreadCategoryEnum category,
            @PathVariable("status") ToxicStatusEnum newToxicStatus,
            @RequestParam("reason") String reason) {
        try {
            modService.banAThreadWhileSurfing(threadId, category, newToxicStatus, reason);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
}
