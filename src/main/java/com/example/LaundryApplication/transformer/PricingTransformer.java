package com.example.LaundryApplication.transformer;

import com.example.LaundryApplication.dto.response.PricingResponse;
import com.example.LaundryApplication.model.Pricing;

import java.util.ArrayList;
import java.util.List;

public class PricingTransformer {

    //List of Pricing into List of Pricing response
    public static List<PricingResponse> pricingListToPricingResponseList(List<Pricing> pricingList){

        List<PricingResponse> pricingResponseList = new ArrayList<>();

        for(Pricing pricing : pricingList){
            pricingResponseList.add(pricingToPricingResponse(pricing));
        }

        return pricingResponseList;

    }

    public static PricingResponse pricingToPricingResponse(Pricing pricing){

        PricingResponse pricingResponse = PricingResponse.builder()
                .garmentType(pricing.getGarmentType())
                .price(pricing.getPrice()).
                build();

        return pricingResponse;
    }
}
