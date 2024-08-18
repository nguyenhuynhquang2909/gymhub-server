package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.mapper.ModeratorMapper;
import com.gymhub.gymhub.repository.ModeratorRepository;
import com.gymhub.gymhub.service.CustomUserDetailsService;
import com.gymhub.gymhub.service.ModService;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * The type Mod controller.
 */
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
    @Autowired
    private ModeratorRepository moderatorRepository;

    /**
     * Gets mod.
     *
     * @param modDTO the mod dto
     * @return the mod
     */
    //mod view profile
    @Operation(description = "This operation returns mod profile information",
            tags = "Mod Profile Page")
    @GetMapping("/{id}")
    public ModeratorRequestAndResponseDTO getMod(
            @RequestBody ModeratorRequestAndResponseDTO modDTO) {
        String modUsername = modDTO.getUsername();
        return (ModeratorRequestAndResponseDTO) customUserDetailsService.loadUserByUsername(modUsername);
    }

    /**
     * Update mod response entity.
     *
     * @param modDTO the mod dto
     * @return the response entity
     */
    //mod update profile
    @Operation(description = "This operation update mod profile information",
            tags = "Mod Profile Page")
    @PutMapping("/update/mod-{id}")
    public ResponseEntity<Void> updateMod(
            @RequestBody ModeratorRequestAndResponseDTO modDTO) {
        return modService.updateModInfo(modDTO);
    }


    //get all post and threads with toxicStatus = pending => show in mod dashboard

    /**
     * Fill mod dashboard response entity.
     *
     * @return the response entity
     */
    @Operation(description = "This operation fills the mod dashboard's with 3 tables: pending posts, pending threads, and banned members",
            tags = "Mod Dashboard Page")
    @GetMapping("/dashboard/mod-{id}")
    public ResponseEntity<ModDashboardTablesResponseDTO> fillModDashboard() {
        List<PostResponseDTO> pendingPosts = modService.getAllPendingPosts();
        List<ThreadResponseDTO> pendingThreads = modService.getAllPendingThreads();
        List<BannedMemberDTO> bannedMembers = modService.displayBannedMembers(modService.getBannedMembers());
        ModDashboardTablesResponseDTO response = new ModDashboardTablesResponseDTO(pendingPosts, pendingThreads, bannedMembers);
        return ResponseEntity.ok(response);
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

        // Get moderator details and construct ModeratorRequestAndResponseDTO
        ModeratorRequestAndResponseDTO modDTO = new ModeratorRequestAndResponseDTO();

        return modService.resolveAPendingPost(modDTO, postId, threadId, newToxicStatus, reason);
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

        // Get moderator details and construct ModeratorRequestAndResponseDTO
        ModeratorRequestAndResponseDTO modDTO = new ModeratorRequestAndResponseDTO();

        return modService.resolveAPendingThread(modDTO, threadId, category, newToxicStatus, reason);
    }

    /**
     * Bans a member for a specified duration with a given reason.
     *
     * @param userId         The ID of the member to be banned.
     * @param durationMillis The duration of the ban in milliseconds.
     * @param reason         The reason for banning the member.
     * @return A response indicating the outcome of the ban operation.
     */
    @Operation(description = "This operation helps mod to ban a member with a duration in milliseconds", tags = "Mod Dashboard Page")
    @PatchMapping("/dashboard/mod-{modId}/ban/member-{memberId}")
    public ResponseEntity<Void> banAMember(@PathVariable("memberId") Long userId,
                                           @RequestParam("duration") Long durationMillis,
                                           @RequestParam("reason") String reason) {
        Date banUntilDate = new Date(System.currentTimeMillis() + durationMillis);
        modService.banMember(userId, banUntilDate, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Un ban a member response entity.
     *
     * @param userId the user id
     * @return the response entity
     */
    @Operation(description = "This operation helps mod to remove a member from a ban list", tags = "Mod Dashboard Page")
    @DeleteMapping("/dashboard/mod-{modId}/unBan/member-{memberId}")
    public ResponseEntity<Void> unBanAMember(@PathVariable("memberId") Long userId) {
        modService.unbanMember(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Ban a post response entity.
     *
     * @param modId          the mod id
     * @param postId         the post id
     * @param threadId       the thread id
     * @param newToxicStatus the new toxic status
     * @param reason         the reason
     * @param modDTO         the mod dto
     * @return the response entity
     */
    @Operation(description = "This operation helps mod to immediately ban a post while surfing the forum", tags = "Mod Surfing Forum")
    @PatchMapping("/dashboard/mod-{modId}/ban/post-{postId}/thread-{threadId}/status-{status}")
    public ResponseEntity<Void> banAPost(
            @PathVariable("modId") Long modId,
            @PathVariable("postId") Long postId,
            @PathVariable("threadId") Long threadId,
            @PathVariable("status") ToxicStatusEnum newToxicStatus,
            @RequestParam("reason") String reason,
            @RequestBody ModeratorRequestAndResponseDTO modDTO)
    {
        // Call the service to ban the post while surfing
        return modService.banAPostWhileSurfing(modDTO, postId, threadId, newToxicStatus, reason);
    }

    /**
     * Ban a thread response entity.
     *
     * @param modId          the mod id
     * @param threadId       the thread id
     * @param category       the category
     * @param newToxicStatus the new toxic status
     * @param reason         the reason
     * @param modDTO         the mod dto
     * @return the response entity
     */
    @Operation(description = "This operation helps mod to immediately ban a thread while surfing the forum", tags = "Mod Surfing Forum")
    @PatchMapping("/dashboard/mod-{modId}/ban/thread-{threadId}/category-{category}/status-{status}")
    public ResponseEntity<Void> banAThread(
            @PathVariable("modId") Long modId,
            @PathVariable("threadId") Long threadId,
            @PathVariable("category") ThreadCategoryEnum category,
            @PathVariable("status") ToxicStatusEnum newToxicStatus,
            @RequestParam("reason") String reason,
            @RequestBody ModeratorRequestAndResponseDTO modDTO)
    {
        // Call the service to ban the thread while surfing
        return modService.banAThreadWhileSurfing(modDTO, threadId, category, newToxicStatus, reason);
    }
}
