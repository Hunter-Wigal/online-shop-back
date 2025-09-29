package com.shop.online_shop.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Entity


@Data
@NoArgsConstructor
@Table(name="transactions")
public class Transaction {
    @Id
    @SequenceGenerator(
            name = "transaction_id_sequence",
            sequenceName = "transaction_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_id_sequence"
    )
    private Integer transactionId;
    private String paypalId;

    // Joins the provided product with a row in the product table
    @ManyToMany(targetEntity = Product.class, cascade = CascadeType.ALL)
    @JoinTable(
            name = "transaction_products",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    // Joins the provided user_id with a user from the user table
    @ManyToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "user_id")
    private User user_id;

    // Array of quantities, used to determine what quantity of which product in the transaction
    private Integer[] quantities;
    private String status;

    public Transaction(Integer transactionId,
                       List<Product> products,
                       User user_id,
                       Integer[] quantities,
                       String status){
        this.transactionId = transactionId;
        this.products = products;
        this.user_id = user_id;
        this.quantities = quantities;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return Objects.equals(transactionId, transaction.transactionId) && Objects.equals(products, transaction.products) && Objects.equals(user_id, transaction.user_id) && Arrays.equals(quantities, transaction.quantities) && Objects.equals(status, transaction.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, products, user_id, Arrays.hashCode(quantities));
    }

    @Override
    public String toString() {
        return "Order{" +
                "transaction_id=" + transactionId +
                ", item_id=" + products +
                ", user_id=" + user_id +
                ", quantity=" + Arrays.toString(quantities) +
                ", status='" + status + '\'' +
                '}';
    }
}
