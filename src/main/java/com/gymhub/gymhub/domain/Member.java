package com.gymhub.gymhub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "Normal users of the forum")
public class Member extends ForumAccount {
    @Setter
    @Column(name = "title", nullable = true, length = 20, unique = false)
    @ApiModelProperty(value = "Title (or Batch) of the user")
    private String title;

    @Setter
    @Column(name = "bio", nullable = true, length = 200, unique = false)
    @ApiModelProperty(value = "A short description of the user")
    private String bio;

    @Setter
    @Lob
    @Transient
    @Column(name = "avatar", nullable = true, updatable = false)
    private byte[] avatar;

    @Setter
    @Column(name = "join_date", nullable = false, updatable = false, unique = false)
    @ApiModelProperty(value = "The date whe the user sign up")
    private Date joinDate;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(
            value = "The last time he user is online",
            notes = "It contains both date and time"
    )
    private LocalDateTime lastSeen;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(
            value = "The number of likes the user's threads and posts have received"
    )
    private int likeCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(
            value = "The number of posts the user has created"
    )
    private int postCount;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(
            value = "The number of followers the user has"
    )
    private int followerCount;


    public Member(String userName, String password, String email, Date joinDate) {
        super(userName, password, email);
        this.joinDate = joinDate;
    }
}
