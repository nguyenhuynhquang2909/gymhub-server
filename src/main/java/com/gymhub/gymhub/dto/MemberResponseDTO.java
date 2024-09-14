package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Base64;
import java.util.Date;
import java.util.Set;

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
    private TitleEnum title;

    @Schema(description = "A short description of the user")
    private String bio;

    @Schema(description = "Avatar of the user encoded as Base64 toString")
    private String avatar;

    @Schema(description = "The date when the user signed up")
    private Date joinDate;

    @Schema(description = "The number of likes the user's threads and posts have received")
    private int likeCount;

    @Schema(description = "The number of posts the user has created")
    private int postCount;

    @Schema(description = "The number of followers the user has")
    private int followerCount;

    @Schema(description = "The number of users the member is following")
    private int followingCount;

    @Schema(description = "List of IDs of users following this member")
    private Set<Long> followerIds;

    @Schema(description = "List of IDs of users this member is following")
    private Set<Long> followingIds;

    @Schema(description = "The date when the member ban is lifted. Null if member is not in the ban list")
    private Date banUntilDate;

    public MemberResponseDTO(Long id, String userName, String email, TitleEnum title, String bio, String avatar, Date joinDate, int likeCount, int postCount, int followerCount, int followingCount, Set<Long> followerIds, Set<Long> followingIds, Date banUntilDate) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.title = title;
        this.bio = bio;
        this.avatar = avatar;
        this.joinDate = joinDate;
        this.likeCount = likeCount;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.followerIds = followerIds;
        this.followingIds = followingIds;
        this.banUntilDate = banUntilDate;
    }
}
