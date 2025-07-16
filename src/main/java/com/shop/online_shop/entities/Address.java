package com.shop.online_shop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="shipping_address")
@Data
@NoArgsConstructor
public class Address {
    // Primary key

    @Id
    private int user_id; // Associated user
    @Id
    private String street_address; // TODO check this, make sure it will always be unique


    private boolean default_switch; // Default shipping address


    private String secondary_street;
    private String city;
    private int state;
    private int country;
    private int zip_code;

}
