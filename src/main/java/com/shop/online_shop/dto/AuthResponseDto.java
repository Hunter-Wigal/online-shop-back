package com.shop.online_shop.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String message;

    public AuthResponseDto(String accessToken, String message){
        this.accessToken = accessToken;
        this.message = message;
    }
}
