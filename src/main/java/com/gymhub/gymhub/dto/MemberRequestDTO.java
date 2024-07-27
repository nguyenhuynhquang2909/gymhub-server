package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "This includes all the member-related field to be included in server's response")
public class MemberRequestDTO {

    @Schema(description = "The username of an account")
    private String userName;

    @Schema(description = "The email of an account")
    private String email;

    @Schema(description = "A short description of the user")
    private String bio;

    @Schema(description = "Avatar of the user encoded as String by base64")
    private String stringAvatar;

    @Schema(description = "The password of an account")
    private String password;
}
