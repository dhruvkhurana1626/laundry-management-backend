package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.enums.GarmentType;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.Garment;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.transformer.GarmentTransformer;
import com.example.LaundryApplication.transformer.OrderTransformer;
import com.example.LaundryApplication.utility.Validation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final Validation validation;
    private final OrderEntityDao orderEntityDao;

    @Transactional
    public OrderResponse createOrder(@Valid OrderRequest orderRequest){

        // 1- Checking if Garment is not added in the Order
        if(orderRequest.getGarmentRequestList()==null || orderRequest.getGarmentRequestList().size()==0){
            throw new BusinessException("Garments Required");
        }

        // 2- Order Request to Order
        OrderEntity orderEntity = OrderTransformer.orderRequestToOrder(orderRequest);

        // 3- GarmentRequest List to Garment List
        List<Garment> garmentList = GarmentTransformer.garmentRequestListToGarmentList(orderRequest.getGarmentRequestList());

        // 4- Calculating Total Amount - Bill
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(Garment garment : garmentList) {

            // Mapping Garment to Order
            garment.setOrder(orderEntity);

            // Calculation price per Item
            BigDecimal pricePerItem = getPrice(garment.getType());

            // Setting price per Item to garment
            garment.setPricePerItem(pricePerItem);

            // Calculating price of Item -> price per item * Quantity
            BigDecimal itemTotal = garment.getPricePerItem()
                    .multiply(BigDecimal.valueOf(garment.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
        }

        // 5- Setting Relationships
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setGarmentList(garmentList);
        orderEntity.setStatus(OrderStatus.RECEIVED);

        // 6- Saving Order
        OrderEntity savedOrder = orderEntityDao.save(orderEntity);

        // 7- Return Order Response
        return OrderTransformer.orderToOrderResponse(savedOrder);
    }

    //setting price per item
    private BigDecimal getPrice(GarmentType garmentType){
        switch (garmentType){
            case SHIRT: return BigDecimal.valueOf(100);
            case PANT: return BigDecimal.valueOf(120);
            case JEANS: return BigDecimal.valueOf(80);
            case COAT_PANT: return BigDecimal.valueOf(1000);
            case BLAZER: return BigDecimal.valueOf(600);
            case SAREE: return BigDecimal.valueOf(1500);
            case LEHENGA: return BigDecimal.valueOf(2500);
            default: return BigDecimal.valueOf(500);
        }
    }
}
