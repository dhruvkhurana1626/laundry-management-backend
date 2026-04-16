package com.example.LaundryApplication.ecxeption;

public class BusinessException extends Throwable {
    public BusinessException(String message) {
        super(message);
    }
}
