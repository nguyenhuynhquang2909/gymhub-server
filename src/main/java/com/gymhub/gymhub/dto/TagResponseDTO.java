package com.gymhub.gymhub.dto;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class TagResponseDTO {
    private Long id;
    private String tagName;

    public TagResponseDTO(Long id, String tagName) {
        this.id = id;
        this.tagName = tagName;
    }

    public TagResponseDTO() {
    }
}