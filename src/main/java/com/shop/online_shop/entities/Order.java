package com.shop.online_shop.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Entity

//TODO test what this does

@Data
@NoArgsConstructor
@Table(name="transactions")
public class Order {
    @Id
    @SequenceGenerator(
            name = "order_id_sequence",
            sequenceName = "order_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_id_sequence"
    )
    private Integer transaction_id;

    // Change to be a list of products
    @OneToOne(targetEntity = Product.class, cascade = CascadeType.ALL)
    @JoinColumn(name="product", referencedColumnName = "id")
    private Product product;

    // One to one
    @OneToOne(targetEntity = UserEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "user_id")
    private UserEntity user_id;

    private Integer quantity;
    private String status;

    public Order(Integer transaction_id,
                 Product product,
                 UserEntity user_id,
                 Integer quantity,
                 String status){
        this.transaction_id = transaction_id;
        this.product = product;
        this.user_id = user_id;
        this.quantity = quantity;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(transaction_id, order.transaction_id) && Objects.equals(product, order.product) && Objects.equals(user_id, order.user_id) && Objects.equals(quantity, order.quantity) && Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction_id, product, user_id, quantity);
    }

    @Override
    public String toString() {
        return "Order{" +
                "transaction_id=" + transaction_id +
                ", item_id=" + product +
                ", user_id=" + user_id +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}
