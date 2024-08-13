package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.service.MemberService;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@Tag(name = "Member Request Handler", description = "Handlers for members related requests")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private UserService userService;

    @Operation(description = "This operation returns member information")
    @GetMapping("/{id}")
    public MemberResponseDTO getMember(
            @RequestBody MemberRequestDTO memberRequestDTO) {
        String memberUsername = memberRequestDTO.getUserName();
        return (MemberResponseDTO) memberService.loadUserByUsername(memberUsername);
    }

    @Operation(description = "This operation changes the information of member")
    @PostMapping("/update/member-{id}")
    public ResponseEntity<Void> updateMember(
            @RequestBody MemberRequestDTO memberRequestDTO
    ) {
        return memberService.updateMemberInfo(memberRequestDTO);
    }

    //show all notifications => notificationDTO
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

    //create private conversation between 2 user


}
