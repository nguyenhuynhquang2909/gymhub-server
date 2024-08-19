package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This defines all thread-related fields that will be included in server's response")
public class ThreadResponseDTO {
    @Setter
    @Schema(description = "Id of the post")
    private Long id;

    @Setter
    @Schema(description = "The date and time the thread is created")
    private Long creationDateTime;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "like count of the thread")
    private int likeCount;

    @Setter
    @Schema(description = "View count of the thread")
    private int viewCount;

    @Setter
    @Schema(description = "true if the thread has been liked by the user")
    private boolean beenLiked;

    @Setter
    @Schema(description = "The post count of the thread")
    private int postCount;

    @Setter
    @Schema(description = "Name of the author of the thread")
    private String authorName;

    @Setter
    @Schema(description = "Id of the author of the thread")
    private Long authorId;

    @Setter
    @Schema(description = "Encoded avatar (Base64) of the author of the thread")
    private String authorAvatar;

    @Schema(description = "Title of the thread")
    private String title;


    @Schema(description = "The current toxic status of a thread. Front-end use this to choose who to display this thread to")
    private ToxicStatusEnum toxicStatus;

    @Schema(description = "The current resolve status of a thread (if it had been processed by mod. Hide report button if true")
    private boolean resolveStatus;


    @Schema(description = "Only display this if the thread is currently being reported by member, ban by AI or ban by mod")
    private String reason;


    public ThreadResponseDTO(Long id, Long creationDateTime, int likeCount, int viewCount, boolean beenLiked, int postCount, String authorName, Long authorId, String authorAvatar, String title, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        this.id = id;
        this.creationDateTime = creationDateTime;
        this.likeCount = likeCount;
        this.viewCount = viewCount;

        this.beenLiked = beenLiked;
        this.postCount = postCount;
        this.authorName = authorName;
        this.authorId = authorId;
        this.authorAvatar = authorAvatar;
        this.title = title;
        this.toxicStatus = toxicStatus;
        this.resolveStatus = resolveStatus;
        this.reason = reason;
    }
}
