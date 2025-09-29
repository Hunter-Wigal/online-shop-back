package com.shop.online_shop.repositories;

import com.shop.online_shop.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByPaypalId(String paypal_Id);
}
