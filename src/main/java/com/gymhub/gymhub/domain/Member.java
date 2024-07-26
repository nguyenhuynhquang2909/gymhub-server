package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Member")
@Schema(description = "Normal users of the forum")
public class Member extends ForumAccount {
    @Setter
    @Column(name = "title", nullable = true, length = 20, unique = false)
    @Schema(description = "Title (or Batch) of the user")
    private String title;

    @Setter
    @Column(name = "bio", nullable = true, length = 200, unique = false)
    @Schema(description = "A short description of the user")
    private String bio;

    @Setter
    @Lob
    @JsonIgnore
    @Column(name = "avatar", nullable = true, updatable = false)
    private byte[] avatar;

    @Setter
    @Transient
    @Schema(description = "Avatar of the user encoded as String by base64")
    private String stringAvatar;

    @Setter
    @Column(name = "join_date", nullable = false, updatable = false, unique = false)
    @Schema(description = "The date whe the user sign up")
    private Date joinDate;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "The last time he user is online, both date and time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime lastSeen;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "The number of likes the user's threads and posts have received",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private int likeCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "The number of posts the user has created",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private int postCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "The number of followers the user has",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private int followerCount;


    public Member(String userName, String password, String email, Date joinDate) {
        super(userName, password, email);
        this.joinDate = joinDate;
    }
}
