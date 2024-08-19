package com.gymhub.gymhub.dto;

import com.gymhub.gymhub.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

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


    public ThreadRequestDTO(Long id, String title, ThreadCategoryEnum category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }

    public ThreadRequestDTO() {
    }
}
