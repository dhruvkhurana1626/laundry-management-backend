package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.DashboardResponse;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.enums.GarmentType;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.Garment;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.transformer.GarmentTransformer;
import com.example.LaundryApplication.transformer.OrderTransformer;
import com.example.LaundryApplication.utility.Email;
import com.example.LaundryApplication.utility.Validation;
import jakarta.persistence.criteria.Order;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<OrderResponse> getOrders(OrderStatus status, String search, Integer days, int page) {

        Pageable pageable = PageRequest.of(page,10,Sort.by("createdAt").descending());
        Page<OrderEntity> orderEntityPage = orderEntityDao.findAll(pageable);

        List<OrderEntity> orders = orderEntityPage.getContent();

        return orders.stream()
                .filter(order -> status == null || order.getStatus() == status)
                .filter(order -> {
                    if(search == null || search.isBlank()) { return true; }
                    String value = search.toLowerCase();
                    return order.getCustomerName().toLowerCase().contains(value)
                            || order.getPhone().contains(value)
                             || order.getEmail().toLowerCase().contains(value);
                })
                .filter(order -> {
                    if (days == null) return true;
                    LocalDateTime limit = LocalDateTime.now().minusDays(days);
                    return order.getCreatedAt().isAfter(limit);
                })
                .map(OrderTransformer::orderToOrderResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(String id, OrderStatus status) {
        OrderEntity order = validation.findOrderById_ReturnOrder(id);

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

    public DashboardResponse getDashboard() {

        LocalDateTime startOfMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .atStartOfDay();

        List<OrderEntity> orderEntityList =
                orderEntityDao.findAll()
                        .stream()
                        .filter(order -> !order.getCreatedAt().isBefore(startOfMonth))
                        .toList();

        DashboardResponse dashboardResponse = new DashboardResponse();

        //Total Orders
        dashboardResponse.setTotalOrders((long) orderEntityList.size());

        //Total Revenue
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for(OrderEntity orderEntity : orderEntityList){
            totalRevenue = totalRevenue.add(orderEntity.getTotalAmount());
        }

        dashboardResponse.setTotalRevenue(totalRevenue);

        //Orders per Status
        Map<OrderStatus,Long> statusCount = new HashMap<>();

        long RECEIVED  = 0;
        long PROCESSING = 0;
        long READY = 0;
        long DELIVERED = 0;

        for(OrderEntity orderEntity : orderEntityList) {
            if(orderEntity.getStatus()==OrderStatus.DELIVERED) DELIVERED++;
            if(orderEntity.getStatus()==OrderStatus.PROCESSING) PROCESSING++;
            if(orderEntity.getStatus()==OrderStatus.RECEIVED) RECEIVED++;
            if(orderEntity.getStatus()==OrderStatus.READY) READY++;
        }

        statusCount.put(OrderStatus.PROCESSING,PROCESSING);
        statusCount.put(OrderStatus.DELIVERED,DELIVERED);
        statusCount.put(OrderStatus.RECEIVED,RECEIVED);
        statusCount.put(OrderStatus.READY,READY);

        dashboardResponse.setOrdersPerStatus(statusCount);

        return dashboardResponse;
    }

    public List<OrderResponse> getRecentOrders(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderEntity> orderEntityPage = orderEntityDao.findAll(pageable);

        return orderEntityPage.getContent()
                .stream()
                .map(OrderTransformer::orderToOrderResponse)
                .toList();

    }

    public void deleteOrder(String orderId) {
       OrderEntity order = validation.findOrderById_ReturnOrder(orderId);
       orderEntityDao.delete(order);
    }
}
