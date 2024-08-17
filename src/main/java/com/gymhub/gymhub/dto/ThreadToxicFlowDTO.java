package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This class contains all information required for member to report or complain and for mod to ban or display a thread")
public class ThreadToxicFlowDTO {
    @Schema(description = "The id of the thread being affected")
    private Long id;
    @Schema(description = "The reason for the decision ")
    private String reason;
    @Schema(description = "This indicates the category it belongs to")
    private ThreadCategoryEnum threadCategory;
    @Schema(description = "The toxicStatus of the thread: [PENDING,NOT-TOXIC, TOXIC]")
    private ToxicStatusEnum toxicStatus;
    @Schema(description = "The resolveStatus (by mod) of the thread ")
    private boolean resolveStatus;


    public ThreadToxicFlowDTO(Long id, String reason, ThreadCategoryEnum threadCategory, ToxicStatusEnum toxicStatus, boolean resolveStatus) {
        this.id = id;
        this.reason = reason;
        this.threadCategory = threadCategory;
        this.toxicStatus = toxicStatus;
        this.resolveStatus = resolveStatus;
    }
}
