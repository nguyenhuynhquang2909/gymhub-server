package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This defines all member-related fields included in server's response")
public class MemberResponseDTO {

    @Schema(description = "The id of an account")
    private Long id;

    @Schema(description = "The username of an account")
    private String userName;

    @Schema(description = "The email of an account")
    private String email;

    @Schema(description = "Title (or Batch) of the user")
    private String title;

    @Schema(description = "A short description of the user")
    private String bio;

    @Schema(description = "Avatar of the user encoded as String by base64")
    private String stringAvatar;

    @Schema(description = "The date whe the user sign up")
    private Date joinDate;

    @Schema(description = "The last time he user is online, both date and time")
    private LocalDateTime lastSeen;

    @Schema(description = "The number of likes the user's threads and posts have received")
    private int likeCount;

    @Schema(description = "The number of posts the user has created")
    private int postCount;

    @Schema(description = "The number of followers the user has")
    private int followerCount;
}
