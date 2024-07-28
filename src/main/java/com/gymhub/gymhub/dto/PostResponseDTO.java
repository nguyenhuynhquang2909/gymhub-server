package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This defines all post-related fields to be sent to clients")
public class PostResponseDTO {

    @Setter
    @Schema(description = "Id of the post")
    private Long id;

    @Setter
    @Schema(description = "The date and time the thread is created")
    private LocalDateTime creationDateTime;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "like count of the thread")
    private int likeCount;

    @Setter
    @Schema(description = "View count of the thread")
    private int viewCount;

    @Setter
    @Schema(description = "True if the thread has been reported")
    private boolean beenReport;

    @Setter
    @Schema(description = "true if the thread has been liked by the user")
    private boolean beenLiked;

    @Setter
    @Schema(description = "The post count of the Thread")
    private int postCount;

    @Setter
    @Schema(description = "name of the author of the thread")
    private String authorName;

    @Setter
    @Schema(description = "id of the author of the thread")
    private String authorId;

    @Setter
    @Schema(description = "Encoded avatar (Base64) of the author of the thread")
    private String authorAvatar;

    @Schema(description = "Content of the thread")
    private String name;

}
