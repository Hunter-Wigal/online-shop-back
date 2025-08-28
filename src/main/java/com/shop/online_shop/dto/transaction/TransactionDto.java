package com.shop.online_shop.dto.transaction;

public class TransactionDto {
    public Integer[] product_ids;
    // Probably need to switch to username and have the server lookup id
    public String user_email;
    public Integer[] quantities;
    // Should be set by the server or admin
    //    public String status;
    public String address;
}
