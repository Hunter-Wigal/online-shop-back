package com.shop.online_shop.repositories;

import java.util.Optional;

import com.shop.online_shop.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {

//    Optional<Address> findByUser_Id(int id);
//    Boolean existsByUser_Id(int id);
}
