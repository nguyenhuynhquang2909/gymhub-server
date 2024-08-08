package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This class contains all information the reporting request has to include")
public class ReportRequestDTO {
    @Schema(description = "The id of the thread or post being reported")
    Long id;
    @Schema(description = "The reason for the report")
    String reason;
    @Schema(description = "If the subject is a thread, this indicates the category it belongs to")
    ThreadCategoryEnum threadCategory;
    @Schema(description = "The original status of the thread or post: 1 - non-toxic, 0 - pending")
    int from;
    @Schema(description = "The changed status of the thread or post: 1 - non-toxic, 0 - pending")
    int to;
}
