package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.ResourceNotFoundException;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.Garment;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.transformer.GarmentTransformer;
import com.example.LaundryApplication.transformer.OrderTransformer;
import com.example.LaundryApplication.utility.Email;
import com.example.LaundryApplication.utility.OrderSpecification;
import com.example.LaundryApplication.utility.Validation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final Validation validation;
    private final OrderEntityDao orderEntityDao;
    private final Email email;
    private final PricingService pricingService;

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
            BigDecimal pricePerItem = pricingService.getPrice(garment.getType());

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
        orderEntity.setUser(validation.getCurrentUser());

        // 6- Saving Order
        OrderEntity savedOrder = orderEntityDao.save(orderEntity);

        // 7- Return Order Response
        return OrderTransformer.orderToOrderResponse(savedOrder);
    }

    public Page<OrderResponse> getOrders(
            Integer id,
            OrderStatus status,
            String search,
            Integer days,
            int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        User user = validation.getCurrentUser();

        System.out.println(user.getId());;
        System.out.println(user.getEmail());

        // Build specification containing DB-level filters
        Specification<OrderEntity> spec = OrderSpecification.buildFilterSpec(id,
                status,
                search,
                days,
                user.getId());

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

        if (!order.getUser().getId().equals(validation.getCurrentUser().getId())) {
            throw new BusinessException("You don't have permission to make changes");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException(
                    "Delivered order status cannot be changed"
            );
        }

         if(status==OrderStatus.DELIVERED){
             CompletableFuture.runAsync(() -> {
                 email.sendEmailWhenOrderDelivered(order);
             }).exceptionally(ex -> {
                 log.error("Failed to send DELIVERED email for order {}", order.getId(), ex);
                 return null;
             });
        }

        order.setStatus(status);
        return OrderTransformer.orderToOrderResponse(order);
    }

    public List<OrderResponse> getRecentOrders(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Long userId = validation.getCurrentUser().getId();

        Page<OrderEntity> orderEntityPage =
                orderEntityDao.findByUserId(userId, pageable);

        return orderEntityPage.getContent()
                .stream()
                .map(OrderTransformer::orderToOrderResponse)
                .toList();

    }

    public void deleteOrder(Integer orderId) {
       OrderEntity order = validation.findOrderById_ReturnOrder(orderId);

       if(order.getStatus()==OrderStatus.DELIVERED){
           throw new BusinessException("You cannot delete this order");
       }

        if (!order.getUser().getId().equals(validation.getCurrentUser().getId())) {
            throw new BusinessException("You don't have permission to delete this Order");
        }

       orderEntityDao.delete(order);
    }
}
