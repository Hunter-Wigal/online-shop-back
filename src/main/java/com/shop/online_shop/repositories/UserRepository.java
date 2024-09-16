package com.shop.online_shop.repositories;

import java.util.Optional;
import com.shop.online_shop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

        Optional<User> findByEmail(String email);
        Boolean existsByEmail(String email);
}
