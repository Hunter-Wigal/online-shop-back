package com.shop.online_shop.dto.user;

import com.shop.online_shop.entities.Product;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CartGetDto {
    public List<Product> products;
    public List<Integer> quantities;

}
