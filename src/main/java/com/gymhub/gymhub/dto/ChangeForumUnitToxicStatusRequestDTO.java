package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This class contains all information for the request from mod to change the toxic status of a thread or a post")
public class ChangeForumUnitToxicStatusRequestDTO {
    @Schema(description = "The id of the thread or post being reported")
    Long id;
    @Schema(description = "The reason for the mod decisions")
    String reason;
    @Schema(description = "The newly changed status of the thread or post: \"NOT-TOXIC\" OR \"TOXIC\"")
    String newStatus;
}
