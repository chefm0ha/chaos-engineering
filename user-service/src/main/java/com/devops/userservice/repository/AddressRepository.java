package com.devops.userservice.repository;

import com.devops.userservice.model.entity.Address;
import com.devops.userservice.model.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdAndActiveTrue(Long userId);

    Optional<Address> findByUserIdAndIsDefaultTrueAndActiveTrue(Long userId);

    List<Address> findByUserIdAndTypeAndActiveTrue(Long userId, AddressType type);

    Optional<Address> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
}