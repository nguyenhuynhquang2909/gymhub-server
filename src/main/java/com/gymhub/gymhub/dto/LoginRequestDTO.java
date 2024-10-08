package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "This includes all fields clients have to include when sending log-in request ")
public class LoginRequestDTO {
    @Schema(description = "The username of the client")
    private String username;
    @Schema(description = "The password of the client")
    private String password;
}
