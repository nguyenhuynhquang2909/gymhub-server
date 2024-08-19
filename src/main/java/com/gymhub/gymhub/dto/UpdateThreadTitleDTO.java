package com.gymhub.gymhub.dto;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UpdateThreadTitleDTO {
    private Long ThreadId;
    private String title;

    public UpdateThreadTitleDTO(Long threadId, String title) {
        ThreadId = threadId;

        this.title = title;
    }
}