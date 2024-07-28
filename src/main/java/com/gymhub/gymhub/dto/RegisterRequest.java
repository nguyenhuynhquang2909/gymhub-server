package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This includes all fields that clients need to include in request body when sending register request")
public class RegisterRequest {
    @Schema(description = "The username of the client")
    private String userName;
    @Schema(description = "The password of the client")
    private String password;
    @Schema(description = "The email of the client")
    private String email;
}
