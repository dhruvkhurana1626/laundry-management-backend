package com.example.LaundryApplication.dao;

import com.example.LaundryApplication.enums.GarmentType;
import com.example.LaundryApplication.model.Pricing;
import com.example.LaundryApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PricingRepository
        extends JpaRepository<Pricing, Long> {

    Optional<Pricing> findByGarmentType(GarmentType garmentType);
    Optional<Pricing> findByUserIdAndGarmentType(Long id,GarmentType garmentType);
    List<Pricing> findByUserId(Long id);

    Optional<Pricing> findByGarmentTypeAndUser(GarmentType garmentType, User user);
}