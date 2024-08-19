package com.gymhub.gymhub.dto;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Schema(description = "This defines all thread-related fields that clients need to send in request body")
public class ThreadRequestDTO {

    @Schema(description = "The id of the thread")
    private Long id;


    @Schema(description = "The title of the thread")
    private String title;

    @Schema(description = "The category the thread belongs to (FLEXING, ADVISE, SUPPLEMENT) ")
    private ThreadCategoryEnum category;


    @Schema(description = "The group of tags that the thread belong to ")
    private Set<Tag> tags;

    public ThreadRequestDTO(Long id, String title, ThreadCategoryEnum category, Set<Tag> tags) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.tags = tags;
    }

    public ThreadRequestDTO() {
    }
}
