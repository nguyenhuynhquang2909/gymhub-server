package com.gymhub.gymhub.dto;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UpdateThreadTitleDTO {
    private Long ThreadId;
    private Long authorId;
    private String title;

    public UpdateThreadTitleDTO(Long threadId, Long authorId, String title) {
        ThreadId = threadId;
        this.authorId = authorId;
        this.title = title;
    }
}