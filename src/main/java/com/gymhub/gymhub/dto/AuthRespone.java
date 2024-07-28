package com.gymhub.gymhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "This includes all fields that server will send in response to log in requests")
public class AuthRespone {
    @Schema(description = "The authentication token")
    private String accessToken;
    @Schema(description = "Token type")
    private String tokenType = "Bearer";

    public AuthRespone(String accessToken) {
        this.accessToken = accessToken;
    } 
}
