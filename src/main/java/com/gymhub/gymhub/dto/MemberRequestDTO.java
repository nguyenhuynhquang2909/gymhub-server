package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
@Schema(description = "This includes all the member-related field to be included in client request body")
public class MemberRequestDTO {
    @Schema(description = "The member Id")
    private  Long id;


    @Schema(description = "The username of an account")
    private String userName;

    @Schema(description = "The email of an account")
    private String email;

    @Schema(description = "A short description of the user")
    private String bio;

    @Schema(description = "The password of an account")
    private String password;

    public MemberRequestDTO(Long id, String userName, String email, String bio, String stringAvatar, String password) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.bio = bio;
        this.password = password;
    }

    public MemberRequestDTO() {
    }


    public MemberRequestDTO(Long id) {
    }
}
