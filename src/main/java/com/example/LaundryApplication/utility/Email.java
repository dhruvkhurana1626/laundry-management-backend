package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.model.Garment;
import com.example.LaundryApplication.model.OrderEntity;
import jakarta.mail.MessagingException;
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
    public void sendEmailWhenOrderDelivered(OrderEntity order){
        String to = order.getEmail();

        Integer orderId = order.getId();
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
            throw new BusinessException("Failed to send - Order is Deleivered email");
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

    public void sendPasswordResetEmail(String email, String resetLink) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject("Reset Your Password");
            helper.setText(buildPasswordResetHtml(resetLink), true); // true = enables HTML content

            javaMailSender.send(mimeMessage);
        }
        catch (RuntimeException | MessagingException e){
            throw new BusinessException("Failed to send - Reset Email for User with email:- " + email);
        }

    }

    private String buildPasswordResetHtml(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Password</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="padding: 40px 0;">
                    <tr>
                        <td align="center">
                            <table border="0" cellpadding="0" cellspacing="0" width="500" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden;">
                                
                                <!-- Header -->
                                <tr>
                                    <td align="center" style="background-color: #2563eb; padding: 30px; color: #ffffff;">
                                        <h1 style="margin: 0; font-size: 22px; font-weight: 600; letter-spacing: 0.5px;">Laundry App</h1>
                                    </td>
                                </tr>

                                <!-- Body -->
                                <tr>
                                    <td style="padding: 40px 30px; color: #334155;">
                                        <h2 style="margin: 0 0 16px 0; font-size: 18px; color: #0f172a;">Password Reset Request</h2>
                                        <p style="margin: 0 0 20px 0; font-size: 14px; line-height: 1.6; color: #475569;">
                                            We received a request to reset your password. Click the button below to set a new password for your account.
                                        </p>
                                        
                                        <!-- Call to Action Button -->
                                        <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="margin: 30px 0;">
                                            <tr>
                                                <td align="center">
                                                    <a href="%s" target="_blank" style="display: inline-block; padding: 14px 28px; background-color: #2563eb; color: #ffffff; text-decoration: none; font-size: 14px; font-weight: 600; border-radius: 6px; box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);">
                                                        Reset Password
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>

                                        <p style="margin: 0 0 10px 0; font-size: 13px; line-height: 1.5; color: #64748b;">
                                            This link is valid for a limited time. If you didn't request a password reset, you can safely ignore this email.
                                        </p>
                                        
                                        <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 25px 0;">

                                        <!-- Fallback Link -->
                                        <p style="margin: 0; font-size: 12px; color: #94a3b8; word-break: break-all;">
                                            Having trouble clicking the button? Copy and paste this URL into your web browser:<br>
                                            <a href="%s" style="color: #2563eb; text-decoration: underline;">%s</a>
                                        </p>
                                    </td>
                                </tr>

                                <!-- Footer -->
                                <tr>
                                    <td align="center" style="background-color: #f8fafc; padding: 20px; font-size: 12px; color: #94a3b8; border-top: 1px solid #f1f5f9;">
                                        &copy; %d Laundry Application. All rights reserved.
                                    </td>
                                </tr>

                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(resetLink, resetLink, resetLink, java.time.Year.now().getValue());
    }
}
