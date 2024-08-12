package com.shop.online_shop.repositories;

import com.shop.online_shop.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Transaction, Integer> {
}
