package com.example.LaundryApplication.ecxeption;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Getter
@Setter
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler (ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException ex) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler (ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler (BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(Exception ex) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler (InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception ex) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }



    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpStatus status) {

        ErrorResponse error = new ErrorResponse(
                message,
                status.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, status);
    }
}