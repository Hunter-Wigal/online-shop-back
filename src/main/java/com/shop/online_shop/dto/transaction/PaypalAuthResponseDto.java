package com.shop.online_shop.dto.transaction;

import lombok.Data;

@Data
public class PaypalAuthResponseDto {
    String scope;
    String access_token;
    String token_type;
    String app_id;
    String expires_in;
    String nonce;
}
