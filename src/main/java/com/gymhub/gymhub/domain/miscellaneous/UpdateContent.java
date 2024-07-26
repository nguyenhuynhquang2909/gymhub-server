package com.gymhub.gymhub.domain.miscellaneous;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateContent {

    private String content;
    private List<String> encodedImage;

    public UpdateContent(String content, List<String> encodedImage) {
        this.content = content;
        this.encodedImage = encodedImage;
    }
}
