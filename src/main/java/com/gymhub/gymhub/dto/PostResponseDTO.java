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
@Schema
public class PostResponseDTO {

    @Setter
    @Schema(description = "Id of the post")
    private Long id;

    @Setter
    @Schema(description = "The date and time the post is created")
    private LocalDateTime creationDateTime;

    @Setter
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "like count of the post")
    private int likeCount;

    @Setter
    @Schema(description = "View count of the post")
    private int viewCount;

    @Setter
    @Schema(description = "True if the post has been reported")
    private boolean beenReport;

    @Setter
    @Schema(description = "true if the post has been liked by the user")
    private boolean beenLiked;

    @Setter
    @Schema(description = "name of the author of the post")
    private String authorName;

    @Setter
    @Schema(description = "id of the author of the post")
    private String authorId;

    @Setter
    @Schema(description = "Encoded avatar (Base64) of the author of the post")
    private String authorAvatar;

    @Schema(description = "Content of the post")
    private String content;

    @Setter
    @Schema(description = "List of images included in the post content encoded as Strings")
    private List<String> encodedImages = new ArrayList<>();

    @Setter
    @Schema(description = "Id of the thread the post belongs to")
    private String threadId;


}
