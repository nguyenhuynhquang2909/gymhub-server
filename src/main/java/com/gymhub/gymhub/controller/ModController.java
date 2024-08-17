package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.*;
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

    //mod view profile
    @Operation(description = "This operation returns mod profile information",
            tags = "Mod Profile Page")
    @GetMapping("/{id}")
    public ModeratorRequestAndResponseDTO getMod(
            @RequestBody ModeratorRequestAndResponseDTO modDTO) {
        String modUsername = modDTO.getUsername();
        return (ModeratorRequestAndResponseDTO) customUserDetailsService.loadUserByUsername(modUsername);
    }

    //mod update profile
    @Operation(description = "This operation update mod profile information",
            tags = "Mod Profile Page")
    @GetMapping("/update/mod-{id}")
    public ResponseEntity<Void> updateMod(
            @RequestBody ModeratorRequestAndResponseDTO modDTO) {
        return modService.updateModInfo(modDTO);
    }


    //get all post and threads with toxicStatus = pending => show in mod dashboard

    @Operation(description = "This operation fills the mod dashboard's with 3 tables: pending posts, pending threads, and banned members",
            tags = "Mod Dashboard Page")
    @GetMapping("/dashboard/mod-{id}")
    public ResponseEntity<ModDashboardTablesResponseDTO> fillModDashboard() {
        List<PostResponseDTO> pendingPosts = modService.getAllPendingPosts();
        List<ThreadResponseDTO> pendingThreads = modService.getAllPendingThreads();
        List<BannedMemberDTO> bannedMembers = modService.displayBannedMembers(modService.getBannedMembers());
                //how to display a row of bannedMembers?=> username, ban until, reasons
        ModDashboardTablesResponseDTO response = new ModDashboardTablesResponseDTO(pendingPosts, pendingThreads, bannedMembers);
        return ResponseEntity.ok(response);
    }
    //decide if a pending post is toxic or not toxic


    //delete all toxic post from database



//    //decide if a pending thread  is toxic or not toxic
//    @Operation(description = "This operation help mod to change toxicStatus of a pending thread",
//            tags = "Mod Dashboard Page")
//    @GetMapping("/dashboard/mod-{id}/thread-{id}/decide{NEW-STATUS}")
//    public List<PostResponseDTO> changeToxicStatusOfAPendingThread() {
//        return modService.changeToxicStatusOfAThread();
//    }
    //delete group of toxic thread


    //ban a member

    /**
     * Bans a member for a specified duration with a given reason.
     *
     * @param userId The ID of the member to be banned.
     * @param durationMillis The duration of the ban in milliseconds.
     * @param reason The reason for banning the member.
     * @return A response indicating the outcome of the ban operation.
     */
    @Operation(description = "This operation helps mod to ban a member with a duration in milliseconds", tags = "Mod Dashboard Page")
    @GetMapping("/dashboard/mod-{modId}/ban/member-{memberId}")
    public ResponseEntity<Void> banAMember(@PathVariable("memberId") Long userId,
                                           @RequestParam("duration") Long durationMillis,
                                           @RequestParam("reason") String reason) {
        Date banUntilDate = new Date(System.currentTimeMillis() + durationMillis);
        modService.banMember(userId, banUntilDate, reason);
        return ResponseEntity.ok().build();
    }
    //unban a member

    @Operation(description = "This operation helps mod to remove a member from a ban list", tags = "Mod Dashboard Page")
    @GetMapping("/dashboard/mod-{modId}/unBan/member-{memberId}")
    public ResponseEntity<Void> unBanAMember(@PathVariable("memberId") Long userId) {
        modService.unbanMember(userId);
        return ResponseEntity.ok().build();
    }

    //ban a post immediately

    @Operation(description = "This operation helps mod to immediately ban a post while surfing the forum ", tags = "Mod Surfing Forum")
    @GetMapping("/dashboard/mod-{modId}/Ban/post-{postId}")
    public ResponseEntity<Void> banAPost(@PathVariable("postId") Long postId) {
        modService.banAPostWhileSurfing(postId);
        return ResponseEntity.ok().build();
    }


    //ban a thread  immediately

    @Operation(description = "This operation helps mod to immediately ban a thread while surfing the forum ", tags = "Mod Surfing Forum")
    @GetMapping("/dashboard/mod-{modId}/Ban/thread-{threadId}")
    public ResponseEntity<Void> banAThread(@PathVariable("threadId") Long threadId) {
        modService.banAThreadWhileSurfing(threadId);
        return ResponseEntity.ok().build();
    }

}
