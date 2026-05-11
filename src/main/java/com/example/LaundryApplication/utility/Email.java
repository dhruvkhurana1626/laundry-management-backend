package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.model.Garment;
import com.example.LaundryApplication.model.OrderEntity;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class Email {

    @Autowired
    public JavaMailSender javaMailSender;

    @SneakyThrows
    public void sendEmailWhenOrderReady(OrderEntity order){
        String to = order.getEmail();

        String orderId = order.getId();
        String customerName = order.getCustomerName();
        String phoneNumber = order.getPhone();
        BigDecimal totalAmount = order.getTotalAmount();
        LocalDateTime createdAt = order.getCreatedAt();

        String subject = customerName + ", Your Order's Ready | LaundryXClean69" ;

        List<Garment> garmentList = order.getGarmentList();

        String body = """
        
        <div style="
            font-family: Arial, sans-serif;
            padding: 20px;
            background-color: #f4f6f8;
        ">
        
            <div style="
                max-width: 600px;
                margin: auto;
                background: white;
                border-radius: 10px;
                padding: 20px;
                border: 1px solid #ddd;
            ">
        
                <h2 style="
                    color: #2d89ff;
                    text-align: center;
                ">
                    Laundry Order Ready
                </h2>
        
                <p>
                    Hello <b>%s</b>,
                </p>
        
                <p>
                    Your laundry order is now
                    <b style="color: green;">
                        READY
                    </b>
                    for pickup.
                </p>
        
                <hr/>
        
                <h3>Order Details</h3>
        
                <p><b>Order ID:</b> %s</p>
                <p><b>Phone:</b> %s</p>
                <p><b>Total Amount:</b> ₹%s</p>
                <p><b>Created At:</b> %s</p>
        
                <hr/>
        
                <h3>Garments</h3>
        
                %s
        
                <br/>
        
                <p>
                    Thank you for choosing our service.
                </p>
        
                <p style="
                    color: gray;
                    font-size: 12px;
                ">
                    Laundry Management System
                </p>
        
            </div>
        
        </div>
        
        """.formatted(
                customerName,
                orderId,
                phoneNumber,
                totalAmount,
                createdAt.toLocalDate(),
                buildGarmentsTable(garmentList)
        );

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setFrom("laundryxclean69@gmail.com");
            helper.setSubject(subject);
            helper.setText(body,true);

            javaMailSender.send(mimeMessage);
        } catch (RuntimeException e) {
            throw new BusinessException("Failed to send registration email");
        }

    }

    @SneakyThrows
    public void sendEmailWhenOrderDelivered(OrderEntity order){
        String to = order.getEmail();

        String orderId = order.getId();
        String customerName = order.getCustomerName();
        String phoneNumber = order.getPhone();
        BigDecimal totalAmount = order.getTotalAmount();
        LocalDateTime createdAt = order.getCreatedAt();

        String subject = "Order's Delievered | Thanks for trusting us - LaundryXClean69" ;

        List<Garment> garmentList = order.getGarmentList();

        String body = """

        <div style="
            font-family: Arial, sans-serif;
            padding: 20px;
            background-color: #f4f6f8;
        ">

        <div style="
            max-width: 600px;
            margin: auto;
            background: white;
            border-radius: 10px;
            padding: 20px;
            border: 1px solid #ddd;
        ">

        <h2 style="
            color: #2d89ff;
            text-align: center;
        ">
            Laundry Order Delivered
        </h2>

        <p>
            Hello <b>%s</b>,
        </p>

        <p>
            Your laundry order has been
            <b style="color: #27ae60;">
                DELIVERED
            </b>
            successfully.
        </p>

        <hr/>

        <h3>Order Details</h3>

        <p><b>Order ID:</b> %s</p>
        <p><b>Phone:</b> %s</p>
        <p><b>Total Amount:</b> ₹%s</p>
        <p><b>Created At:</b> %s</p>

        <hr/>

        <h3>Garments</h3>

        %s

        <br/>

        <p>
            Thank you for choosing our service.
        </p>

        <p style="
            color: gray;
            font-size: 12px;
        ">
            Laundry Management System
        </p>
        
        </div>
        
        </div> 
        """.formatted(
                customerName,
                orderId,
                phoneNumber,
                totalAmount,
                createdAt.toLocalDate(),
                buildGarmentsTable(garmentList)
        );

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setFrom("laundryxclean69@gmail.com");
            helper.setSubject(subject);
            helper.setText(body,true);

            javaMailSender.send(mimeMessage);
        } catch (RuntimeException e) {
            throw new BusinessException("Failed to send registration email");
        }
    }

    //building a table of Garment Item
    private String buildGarmentsTable(List<Garment> garmentList) {

        StringBuilder table = new StringBuilder();

        table.append("""
                <table style = " width:100%; border-collapse : collapse; ">
                
                 <tr style= "background:#2d89ff;color:white;" >
                
                  <th style="padding:8px;border:1px solid #ddd;"> Type </th>
                  <th style="padding:8px;border:1px solid #ddd;"> Quantity </th>
                
                 </tr>
                                 
                """);

        for(Garment garment : garmentList){

            table.append("""
                    <tr>
                     <td style="padding:8px; border:1px solid #ddd;"> %s </td>
                     <td style="padding:8px; border:1px solid #ddd;"> %s </td>
                    </tr>
                    """.formatted(
                            garment.getType().toString(),
                            garment.getQuantity()
            ));
        }

        table.append("</table>");

        return table.toString();

    }
}
