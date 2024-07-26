package com.gymhub.gymhub.domain.miscellaneous;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Contains the post's new content and images")
public class UpdateContent {

    @Schema(description = "New Content")
    private String content;
    @Schema(description = "New images encoded as Strings")
    private List<String> encodedImage;

    public UpdateContent(String content, List<String> encodedImage) {
        this.content = content;
        this.encodedImage = encodedImage;
    }
}
