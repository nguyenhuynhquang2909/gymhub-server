package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This defines all post-related fields that clients need to send in the request body")
public class PostRequestDTO {
    @Schema(description = "Id of the post")
    private Long postId;
    @Schema(description = "Id of the post's owner")
    private Long ownerId;


    @Schema(description = "Content of the post")
    private String content;

    @Schema(description = "Encoded image included in the post content as a String")
    private byte[] encodedImage; // Changed to a single String for one image

    @Schema(description = "Id of the thread the post belongs to")
    private Long threadId;

    public PostRequestDTO(Long postId, Long ownerId, String content, byte[] encodedImage, Long threadId) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.content = content;
        this.encodedImage = encodedImage;
        this.threadId = threadId;
    }
}
