package com.shop.online_shop.dto.user;

import com.shop.online_shop.entities.Product;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CartGetDto {
    List<Product> products;
    List<Integer> quantities;

}
