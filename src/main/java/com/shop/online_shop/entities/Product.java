package com.shop.online_shop.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public class Product {
    @Id
    @SequenceGenerator(
            name = "product_id_sequence",
            sequenceName = "product_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id_sequence"
    )
    private Integer product_id;
    private String item_name;
    private String description;
    private Double price;

    public Product(Integer product_id,
                String item_name,
                String description,
                Double price){
        this.product_id = product_id;
        this.item_name = item_name;
        this.description = description;
        this.price = price;
    }

    public Product(){
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(product_id, product.product_id) && Objects.equals(item_name, product.item_name) && Objects.equals(description, product.description) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product_id, item_name, description, price);
    }

    @Override
    public String toString() {
        return "product{" +
                "id=" + product_id +
                ", item_name='" + item_name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
