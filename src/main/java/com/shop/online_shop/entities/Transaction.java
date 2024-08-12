package com.shop.online_shop.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity

//TODO test what this does

@Data
@NoArgsConstructor
public class Transaction {
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
    private Integer item_id;
    private Integer user_id;
    private Integer quantity;
    private String status;

    public Transaction(Integer transaction_id,
                       Integer item_id,
                       Integer user_id,
                       Integer quantity,
                       String status){
        this.transaction_id = transaction_id;
        this.item_id = item_id;
        this.user_id = user_id;
        this.quantity = quantity;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return Objects.equals(transaction_id, transaction.transaction_id) && Objects.equals(item_id, transaction.item_id) && Objects.equals(user_id, transaction.user_id) && Objects.equals(quantity, transaction.quantity) && Objects.equals(status, transaction.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction_id, item_id, user_id, quantity);
    }

    @Override
    public String toString() {
        return "Order{" +
                "transaction_id=" + transaction_id +
                ", item_id=" + item_id +
                ", user_id=" + user_id +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}
