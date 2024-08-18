package com.gymhub.gymhub.dto;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UpdateThreadTitleDTO {
    private Long ThreadId;
    private String title;

}