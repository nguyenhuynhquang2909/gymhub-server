package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Contains the post's new content and images")
public class UpdatePostContentDTO {
    @Schema(description = "Post's author ID")
    private Long authorId;
    @Schema(description = "Post's thread ID")
    private Long threadId;
    @Schema(description = "New Content")
    private String content;
    @Schema(description = "New images encoded as Strings")
    private List<String> encodedImage;



    //update both content and image

    public UpdatePostContentDTO(Long authorId, Long threadId, String content, List<String> encodedImage) {
        this.authorId = authorId;
        this.threadId = threadId;
        this.content = content;
        this.encodedImage = encodedImage;
    }
}
