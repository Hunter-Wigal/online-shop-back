package com.shop.online_shop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="shipping_address")
@Data
@NoArgsConstructor
@IdClass(AddressKey.class)
public class Address {
    // Primary key

    @Id
    private int user_id; // Associated user
    @Id
    private String street_address;


    private boolean default_switch; // Default shipping address

    private String secondary_street;
    private String city;
    private String state;
    private String country;
    private int zip_code;

}
