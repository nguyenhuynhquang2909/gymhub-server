package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This defines all post-related fields to be sent to clients")
public class PostResponseDTO {

    @Schema(description = "Id of the post")
    private Long id;

    @Schema(description = "The date and time the thread is created")
    private LocalDateTime creationDateTime;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "like count of the post")
    private int likeCount;

    @Schema(description = "View count of the post")
    private int viewCount;

    @Schema(description = "The current toxicStatus of the post")
    private ToxicStatusEnum toxicStatus;

    @Schema(description = "True if the post has been resolved by mod")
    private boolean resolveStatus;

    @Schema(description = "True if the post has been liked by the user")
    private boolean beenLiked;

    @Schema(description = "The post count of the Thread")
    private int postCount;

    @Schema(description = "Name of the author of the thread")
    private String authorName;

    @Schema(description = "Id of the author of the thread")
    private String authorId;

    @Schema(description = "Encoded avatar (Base64) of the author of the thread")
    private String authorAvatar;

    @Schema(description = "Content of the thread")
    private String name;

    @Schema(description = "Encoded image (Base64) associated with the post")
    private String encodedImage;

    @Schema(description = "The reason for the post's current toxic status. Null if toxic status = NOT-TOXIC" )
    private String reason;


    public PostResponseDTO(Long id, LocalDateTime creationDateTime, int likeCount, int viewCount, ToxicStatusEnum toxicStatus, boolean resolveStatus, boolean beenLiked, int postCount, String authorName, String authorId, String authorAvatar, String name, String encodedImage, String reason) {
        this.id = id;
        this.creationDateTime = creationDateTime;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.toxicStatus = toxicStatus;
        this.resolveStatus = resolveStatus;
        this.beenLiked = beenLiked;
        this.postCount = postCount;
        this.authorName = authorName;
        this.authorId = authorId;
        this.authorAvatar = authorAvatar;
        this.name = name;
        this.encodedImage = encodedImage;
        this.reason = reason;
    }
}
