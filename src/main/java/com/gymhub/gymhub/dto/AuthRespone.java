package com.gymhub.gymhub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRespone {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthRespone(String accessToken) {
        this.accessToken = accessToken;
    } 
}
