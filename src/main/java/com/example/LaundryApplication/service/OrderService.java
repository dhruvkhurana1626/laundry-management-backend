package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.ResourceNotFoundException;
import com.example.LaundryApplication.enums.GarmentType;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.Garment;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.transformer.GarmentTransformer;
import com.example.LaundryApplication.transformer.OrderTransformer;
import com.example.LaundryApplication.utility.Email;
import com.example.LaundryApplication.utility.OrderSpecification;
import com.example.LaundryApplication.utility.Validation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final Validation validation;
    private final OrderEntityDao orderEntityDao;
    private final Email email;

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
            case PANT: return BigDecimal.valueOf(100);
            case JEANS: return BigDecimal.valueOf(150);
            case COAT_PANT: return BigDecimal.valueOf(500);
            case BLAZER: return BigDecimal.valueOf(300);
            case SAREE: return BigDecimal.valueOf(1000);
            case LEHENGA: return BigDecimal.valueOf(2000);
            default: return BigDecimal.valueOf(500);
        }
    }

    public Page<OrderResponse> getOrders(
            Integer id,
            OrderStatus status,
            String search,
            Integer days,
            int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Build specification containing DB-level filters
        Specification<OrderEntity> spec = OrderSpecification.buildFilterSpec(id, status, search, days);

        // Filter and paginate inside the DB execution
        Page<OrderEntity> orderEntityPage = orderEntityDao.findAll(spec, pageable);

        // Map entity page directly to DTO page
        return orderEntityPage.map(OrderTransformer::orderToOrderResponse);
    }

    @Transactional
    public OrderResponse updateStatus(Integer id, OrderStatus status) {
        OrderEntity order = orderEntityDao.findByIdWithGarments(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Order not found"));

        if(status==OrderStatus.READY){
            CompletableFuture.runAsync(()->{
                email.sendEmailWhenOrderReady(order);
            });
        }

         if(status==OrderStatus.DELIVERED){
            CompletableFuture.runAsync(()->{
                email.sendEmailWhenOrderDelivered(order);
            });
        }

        order.setStatus(status);

        return OrderTransformer.orderToOrderResponse(order);
    }

    public List<OrderResponse> getRecentOrders(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderEntity> orderEntityPage = orderEntityDao.findAll(pageable);

        return orderEntityPage.getContent()
                .stream()
                .map(OrderTransformer::orderToOrderResponse)
                .toList();

    }

    public void deleteOrder(Integer orderId) {
       OrderEntity order = validation.findOrderById_ReturnOrder(orderId);
       orderEntityDao.delete(order);
    }
}
