package com.shop.online_shop.entities;

import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

class AddressKey implements Serializable {

    private int user_id; // Associated user

    private String street_address;

    public AddressKey(){

    }

    public AddressKey(int user_id, String street_address) {
        this.user_id = user_id;
        this.street_address = street_address;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AddressKey that = (AddressKey) o;
        return user_id == that.user_id && Objects.equals(street_address, that.street_address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, street_address);
    }
}
