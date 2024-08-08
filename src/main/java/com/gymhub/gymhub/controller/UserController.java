package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.MemberRequestDTO;
import com.gymhub.gymhub.dto.MemberResponseDTO;
import com.gymhub.gymhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User Request Handler", description = "Handlers for members related requests")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(description = "This operation returns user information")
    @GetMapping("/{id}")
    public MemberResponseDTO getMember(@PathVariable Long id) {
//userService.getUserInfo(); //API this line

        return new MemberResponseDTO();
    }

    @Operation(description = "This operation changes the information of users")
    @PostMapping("/update/user-{id}")
    public ResponseEntity<Void> updateMember(
            @Parameter(description = "The id of the user to be updated")
            @PathVariable long id,
            @RequestBody MemberRequestDTO memberRequestDTO
    )
    {
        return ResponseEntity.ok().build();
    }



}
