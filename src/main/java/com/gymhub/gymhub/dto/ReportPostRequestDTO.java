package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This class contains all information required for reporting a post")
public class ReportPostRequestDTO {

    @Schema(description = "The id of the post being reported")
    Long id;

    @Schema(description = "The reason for the report")
    String reason;

    @Schema(description = "The original status of the post: 1 - non-toxic, 0 - pending")
    int from;

    @Schema(description = "The changed status of the post: 1 - non-toxic, 0 - pending")
    int to;
}
