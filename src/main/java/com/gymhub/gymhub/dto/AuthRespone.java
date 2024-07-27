package com.gymhub.gymhub.dto;

public class AuthRespone {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthRespone(String accessToken) {
        this.accessToken = accessToken;
    } 
}
