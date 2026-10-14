package com.example.LaundryApplication.Event;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.utility.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveredEventListener {

    private final OrderEntityDao orderEntityDao;
    private final Email email;

    @Async("emailExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )

    public void handleOrderDelivered(OrderDeliveredEvent event) {

        try {
            OrderEntity order = orderEntityDao
                    .findByIdWithGarments(event.orderId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Order not found: " + event.orderId()
                            )
                    );

            email.sendEmailWhenOrderDelivered(order);

        } catch (Exception ex) {
            log.error(
                    "Failed to send DELIVERED email for order {}",
                    event.orderId(),
                    ex
            );
        }
    }
}