package com.shop.online_shop.dto;

public class OrderDto {
    public Integer[] product_ids;
    // Probably need to switch to username and have the server lookup id
    public String user_email;
    public Integer[] quantities;
    // Should be set by the server or admin
    //    public String status;
    //TODO figure out later
    public String address;
}
