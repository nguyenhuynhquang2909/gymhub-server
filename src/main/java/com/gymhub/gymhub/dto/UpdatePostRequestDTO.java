package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Contains the post's new content and images")
public class UpdatePostRequestDTO {

    @Schema(description = "Post's  ID")
    private Long postId;
    @Schema(description = "Post's thread ID")
    private Long threadId;
    @Schema(description = "New Content")
    private String content;



    public UpdatePostRequestDTO(Long postId, Long threadId, String content) {
        this.postId = postId;
        this.threadId = threadId;
        this.content = content;
    }
}
