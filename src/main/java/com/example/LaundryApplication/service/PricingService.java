package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.PricingRepository;
import com.example.LaundryApplication.dto.response.PricingResponse;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.enums.GarmentType;
import com.example.LaundryApplication.model.Pricing;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.transformer.PricingTransformer;
import com.example.LaundryApplication.utility.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRepository pricingRepository;
    private final Validation validation;

    public BigDecimal getPrice(GarmentType garmentType) {

        User user = validation.getCurrentUser();

        return pricingRepository.findByGarmentTypeAndUser(garmentType,user)
                .map(Pricing::getPrice)
                .orElseThrow(() ->
                        new BusinessException(
                                "Price not configured for " + garmentType
                        ));
    }

    public void updatePrice(GarmentType garmentType, BigDecimal price) {

        User currentUser = validation.getCurrentUser();

        Pricing pricing = pricingRepository
                .findByUserIdAndGarmentType(
                        currentUser.getId(),
                        garmentType
                )
                .orElseThrow(() ->
                        new BusinessException(
                                "Price not configured for " + garmentType
                        ));

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "Price must be greater than zero"
            );
        }

        pricing.setPrice(price);

        pricingRepository.save(pricing);
    }

    public void createDefaultPricing(User user) {

        List<Pricing> pricingList = new ArrayList<>();

        for (GarmentType type : GarmentType.values()) {

            Pricing pricing = new Pricing();

            pricing.setUser(user);
            pricing.setGarmentType(type);
            pricing.setPrice(BigDecimal.valueOf(50));

            pricingList.add(pricing);
        }

        pricingRepository.saveAll(pricingList);
    }

    public List<PricingResponse> getCurrentUserPricing(){

        Long userId = validation.getCurrentUser().getId();
        List<Pricing> pricingList = pricingRepository.findByUserId(userId);

        return PricingTransformer.pricingListToPricingResponseList(pricingList);

    }

}