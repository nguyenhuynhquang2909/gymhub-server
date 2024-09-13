package com.gymhub.gymhub.dto;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Schema(description = "This defines all thread-related fields that clients need to send in request body")
public class ThreadRequestDTO {

    @Schema(description = "The title of the thread")
    private String title;

    @Schema(description = "The category the thread belongs to (FLEXING, ADVISE, SUPPLEMENT) ")
    private ThreadCategoryEnum category;

    @Schema(description = "The group of tags that the thread belong to ")
    private Set<Long> tagSet;

    public ThreadRequestDTO(String title, ThreadCategoryEnum category, Set<Long> tagSet) {
        this.title = title;
        this.category = category;
        this.tagSet = tagSet;
    }

    public ThreadRequestDTO() {
    }
}
