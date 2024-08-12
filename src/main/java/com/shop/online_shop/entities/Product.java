package com.shop.online_shop.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
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
    private Integer id;
    private String item_name;
    private String description;
    private Double price;

    public Product(Integer id,
                String item_name,
                String description,
                Double price){
        this.id = id;
        this.item_name = item_name;
        this.description = description;
        this.price = price;
    }

    public Product(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return item_name;
    }

    public void setItemName(String item_name) {
        this.item_name = item_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(item_name, product.item_name) && Objects.equals(description, product.description) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item_name, description, price);
    }

    @Override
    public String toString() {
        return "product{" +
                "id=" + id +
                ", item_name='" + item_name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
