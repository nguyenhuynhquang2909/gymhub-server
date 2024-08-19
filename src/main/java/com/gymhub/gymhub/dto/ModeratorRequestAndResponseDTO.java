package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "This defines all moderator-related fields for both  request (clients need to send in request body) and response (servers need to send in respond body)")
public class ModeratorRequestAndResponseDTO {
    @Schema(description = "The ID of the moderator")
    private Long id;

    @Schema(description = "The username of the moderator")
    private String username;

    @Schema(description = "The password of the moderator")
    private String password;

    @Schema(description = "The email of the moderator")
    private String email;
}
