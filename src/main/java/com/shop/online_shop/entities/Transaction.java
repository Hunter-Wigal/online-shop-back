package com.shop.online_shop.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Entity

//TODO test what this does

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
    private Integer transaction_id;

    // Change to be a list of products
    @ManyToMany(targetEntity = Product.class, cascade = CascadeType.ALL)
    @JoinTable(
            name = "transaction_products", // This is the join table
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "user_id")
    private User user_id;

    private Integer[] quantities;
    private String status;

    public Transaction(Integer transaction_id,
                       List<Product> products,
                       User user_id,
                       Integer[] quantities,
                       String status){
        this.transaction_id = transaction_id;
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
        return Objects.equals(transaction_id, transaction.transaction_id) && Objects.equals(products, transaction.products) && Objects.equals(user_id, transaction.user_id) && Objects.equals(quantities, transaction.quantities) && Objects.equals(status, transaction.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction_id, products, user_id, Arrays.hashCode(quantities));
    }

    @Override
    public String toString() {
        return "Order{" +
                "transaction_id=" + transaction_id +
                ", item_id=" + products +
                ", user_id=" + user_id +
                ", quantity=" + Arrays.toString(quantities) +
                ", status='" + status + '\'' +
                '}';
    }
}
