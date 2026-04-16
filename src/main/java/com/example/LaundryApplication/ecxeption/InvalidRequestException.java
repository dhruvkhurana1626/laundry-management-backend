package com.example.LaundryApplication.ecxeption;

public class InvalidRequestException extends Throwable {
    public InvalidRequestException(String message) {
        super(message);
    }
}
