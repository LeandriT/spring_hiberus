package com.bank.paymentinitiation.adapter.in.rest;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

/**
 * Manejador global de excepciones para el controlador REST.
 * 
 * Este componente intercepta todas las excepciones lanzadas por los controladores
 * y las convierte en respuestas HTTP apropiadas siguiendo RFC 7807 (Problem Details).
 * 
 * Todas las respuestas de error usan content-type application/problem+json.
 * 
 * @author Payment Initiation Service Team
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePaymentOrderNotFound(PaymentOrderNotFoundException ex) {
        log.warn("Payment order not found: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setTitle("Payment Order Not Found");
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(problemDetail);
    }
    
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
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        StringBuilder details = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
            details.append(error.getField())
                .append(": ")
                .append(error.getDefaultMessage())
                .append("; ")
        );
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            details.toString()
        );
        problemDetail.setTitle("Validation Error");
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(problemDetail);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problemDetail.setTitle("Constraint Violation");
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(problemDetail);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state exception: {}", ex.getMessage(), ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problemDetail.setTitle("Invalid State");
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(problemDetail);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(problemDetail);
    }
}

