package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.service.CustomUserDetailsService;
import com.gymhub.gymhub.service.ModService;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(description = "This operation returns mod information",
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

    @Operation(description = "This operation fills the mod dashboard's pending post and thread tables",
            tags = "Mod Dashboard Page")
    @GetMapping("/dashboard/mod-{id}")
    public ResponseEntity<PendingItemsResponseDTO> getAllPendingPostsAndPendingThread() {
        List<PostResponseDTO> pendingPosts = modService.getAllPendingPosts();
        List<ThreadResponseDTO> pendingThreads = modService.getAllPendingThreads();

        PendingItemsResponseDTO response = new PendingItemsResponseDTO(pendingPosts, pendingThreads);

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


    //ban a user

    //unban a user

    //get all bannedUser table
}
