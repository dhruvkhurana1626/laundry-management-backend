package com.example.LaundryApplication.transformer;

import com.example.LaundryApplication.dto.request.GarmentRequest;
import com.example.LaundryApplication.dto.response.GarmentResponse;
import com.example.LaundryApplication.model.Garment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class GarmentTransformer {

    //garmentRequestList to garmentList
    public static List<Garment> garmentRequestListToGarmentList(@NotEmpty List<@Valid GarmentRequest> garmentRequestList) {
        List<Garment> garmentList = new ArrayList<>();

        for(GarmentRequest garmentRequest : garmentRequestList){
            garmentList.add(GarmentTransformer.garmentRequestToGarment(garmentRequest));
        }
        return garmentList;
    }

    //garmentList to garmentResponseList
    public static List<GarmentResponse> garmentListToGarmentResponseList(@NotEmpty List<@Valid Garment> garmentList){
        List<GarmentResponse> garmentResponseList = new ArrayList<>();

        for(Garment garment : garmentList){
            garmentResponseList.add(GarmentTransformer.garmetToGarmentResponse(garment));
        }

        return garmentResponseList;
    }

    //garment to garmentResponse
    public static GarmentResponse garmetToGarmentResponse(@NotEmpty Garment garment){
        GarmentResponse garmentResponse = GarmentResponse.builder()
                .type(garment.getType())
                .quantity(garment.getQuantity())
                .pricePerItem(garment.getPricePerItem())
                .build();

        return garmentResponse;
    }

    //garmentRequest to garment
    public static Garment garmentRequestToGarment(@NotEmpty GarmentRequest garmentRequest){
        Garment garment = Garment.builder()
                .type(garmentRequest.getType())
                .quantity(garmentRequest.getQuantity())
                .build();

        return garment;
    }
}
