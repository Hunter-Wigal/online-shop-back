package com.shop.online_shop.dto;
import lombok.Data;

public class OrderDto {
    public Integer item_id;
    // Probably need to switch to username and have the server lookup id
    public Integer user_id;
    public Integer quantity;
    // Should be set by the server or admin
//    public String status;
    //TODO figure out later
    public String address;

}
