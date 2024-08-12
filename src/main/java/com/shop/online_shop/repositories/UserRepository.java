package com.shop.online_shop.repositories;

import java.util.Optional;
import com.shop.online_shop.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

        Optional<UserEntity> findByEmail(String email);
        Boolean existsByEmail(String email);
}
