package com.bank.paymentinitiation.adapter.in.rest;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 * 
 * This handler converts domain exceptions to HTTP responses following
 * RFC 7807 (Problem Details for HTTP APIs) using Spring's ProblemDetail.
 * 
 * Responsibilities:
 * - Map domain exceptions to appropriate HTTP status codes
 * - Create ProblemDetail responses with proper structure
 * - Ensure all error responses use content-type application/problem+json
 * - Log exceptions for debugging and monitoring
 * 
 * This is part of the adapter layer and handles exceptions thrown by
 * controllers and use cases.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handles PaymentOrderNotFoundException.
     * 
     * Maps to HTTP 404 NOT FOUND with ProblemDetail containing:
     * - title: "Payment Order Not Found"
     * - status: 404
     * - detail: Exception message
     * - property: paymentOrderReference
     * 
     * @param ex the PaymentOrderNotFoundException
     * @return ResponseEntity with ProblemDetail and 404 status
     */
    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePaymentOrderNotFound(PaymentOrderNotFoundException ex) {
        log.warn("Payment order not found: {}", ex.getPaymentOrderReference());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setTitle("Payment Order Not Found");
        problemDetail.setProperty("paymentOrderReference", ex.getPaymentOrderReference());
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(problemDetail);
    }
    
    /**
     * Handles InvalidPaymentException.
     * 
     * Maps to HTTP 400 BAD REQUEST with ProblemDetail containing:
     * - title: "Invalid Payment Order"
     * - status: 400
     * - detail: Exception message
     * 
     * @param ex the InvalidPaymentException
     * @return ResponseEntity with ProblemDetail and 400 status
     */
    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPayment(InvalidPaymentException ex) {
        log.warn("Invalid payment order: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problemDetail.setTitle("Invalid Payment Order");
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(problemDetail);
    }
    
    /**
     * Handles MethodArgumentNotValidException (Bean Validation errors).
     * 
     * Maps to HTTP 400 BAD REQUEST with ProblemDetail containing:
     * - title: "Bad Request"
     * - status: 400
     * - detail: Validation error message
     * 
     * @param ex the MethodArgumentNotValidException
     * @return ResponseEntity with ProblemDetail and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        
        StringBuilder detail = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            detail.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        });
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            detail.toString().trim()
        );
        problemDetail.setTitle("Bad Request");
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(problemDetail);
    }
    
    /**
     * Handles HttpMessageNotReadableException (malformed JSON, invalid date format, etc.).
     * 
     * Maps to HTTP 400 BAD REQUEST with ProblemDetail containing:
     * - title: "Bad Request"
     * - status: 400
     * - detail: Error message
     * 
     * @param ex the HttpMessageNotReadableException
     * @return ResponseEntity with ProblemDetail and 400 status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed request: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Malformed request. Please check the request format and try again."
        );
        problemDetail.setTitle("Bad Request");
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(problemDetail);
    }
    
    /**
     * Handles generic Exception (catch-all).
     * 
     * Maps to HTTP 500 INTERNAL SERVER ERROR with ProblemDetail containing:
     * - title: "Internal Server Error"
     * - status: 500
     * - detail: Generic error message (does not expose internal details)
     * 
     * This handler catches any unhandled exceptions to prevent exposing
     * internal implementation details to clients.
     * 
     * @param ex the Exception
     * @return ResponseEntity with ProblemDetail and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support if the problem persists."
        );
        problemDetail.setTitle("Internal Server Error");
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(problemDetail);
    }
}

