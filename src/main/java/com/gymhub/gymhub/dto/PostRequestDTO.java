package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This defines all post-related fields that clients need to send in request body")
public class PostRequestDTO {

    @Setter
    @Schema(description = "id of the author of the post")
    private Long authorId;

    @Schema(description = "Content of the post")
    private String content;

    @Setter
    @Schema(description = "List of images included in the post content encoded as Strings")
    private List<String> encodedImages = new ArrayList<>();

    @Setter
    @Schema(description = "Id of the thread the post belongs to")
    private String threadId;

}
