package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Member;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Request Handler", description = "Handlers for members related requests")
public class UserController {

    @GetMapping("/{id}")
    public Member getMember(@PathVariable Long id) {
        return new Member();
    }



}
