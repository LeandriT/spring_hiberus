package com.bank.paymentinitiation.adapter.in.rest;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for REST Controllers.
 * 
 * <p>Handles exceptions and converts them to RFC 7807 Problem Detail responses.
 * All error responses use content-type `application/problem+json`.
 * 
 * <p>This handler follows the Hexagonal Architecture pattern by:
 * <ul>
 *   <li>Catching domain exceptions and converting them to HTTP responses</li>
 *   <li>Catching framework exceptions (Bean Validation, JSON parsing, etc.)</li>
 *   <li>Providing consistent error response format across all endpoints</li>
 * </ul>
 * 
 * <p>Exception Mappings:
 * <ul>
 *   <li>PaymentOrderNotFoundException → 404 NOT FOUND</li>
 *   <li>InvalidPaymentException → 400 BAD REQUEST</li>
 *   <li>MethodArgumentNotValidException → 400 BAD REQUEST (Bean Validation)</li>
 *   <li>HttpMessageNotReadableException → 400 BAD REQUEST (JSON malformado)</li>
 *   <li>Exception → 500 INTERNAL SERVER ERROR (catch-all)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_DETAIL_TYPE = "about:blank";

    /**
     * Handles PaymentOrderNotFoundException.
     * 
     * @param ex the exception
     * @return ResponseEntity with 404 NOT FOUND and ProblemDetail
     */
    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePaymentOrderNotFoundException(
            final PaymentOrderNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TYPE));
        problemDetail.setTitle("Payment Order Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Handles InvalidPaymentException.
     * 
     * @param ex the exception
     * @return ResponseEntity with 400 BAD REQUEST and ProblemDetail
     */
    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPaymentException(
            final InvalidPaymentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TYPE));
        problemDetail.setTitle("Invalid Payment Order");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles MethodArgumentNotValidException (Bean Validation errors).
     * 
     * <p>This exception is thrown when @Valid validation fails on request parameters.
     * Extracts all validation errors and includes them in the ProblemDetail.
     * 
     * <p>⚠️ IMPORTANT: This handler is OBLIGATORY for tests of integration to pass
     * correctly when invalid requests are sent. Without it, Spring Boot returns
     * 500 INTERNAL SERVER ERROR instead of 400 BAD REQUEST.
     * 
     * @param ex the exception
     * @return ResponseEntity with 400 BAD REQUEST and ProblemDetail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException ex) {
        
        // Extract validation errors
        String validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining(", "));
        
        // If no field errors, try object errors
        if (validationErrors.isEmpty()) {
            validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        }
        
        String detail = validationErrors.isEmpty() 
            ? "Validation failed" 
            : "Validation failed: " + validationErrors;
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            detail
        );
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TYPE));
        problemDetail.setTitle("Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles HttpMessageNotReadableException (JSON parsing errors).
     * 
     * <p>This exception is thrown when the JSON request body cannot be parsed
     * (e.g., malformed JSON, invalid date format, invalid enum value).
     * 
     * <p>⚠️ IMPORTANT: This handler is OBLIGATORY for tests of integration to pass
     * correctly when malformed requests are sent. Without it, Spring Boot returns
     * 500 INTERNAL SERVER ERROR instead of 400 BAD REQUEST.
     * 
     * @param ex the exception
     * @return ResponseEntity with 400 BAD REQUEST and ProblemDetail
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex) {
        
        String detail = "Invalid request body format";
        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            // Extract meaningful error message (remove internal details if present)
            String message = ex.getMessage();
            // Take first line if message contains multiple lines
            if (message.contains("\n")) {
                message = message.split("\n")[0];
            }
            // Remove internal class names if present
            if (message.contains(":")) {
                message = message.split(":")[0] + ": " + 
                    message.substring(message.lastIndexOf(":") + 1).trim();
            }
            detail = message.length() > 200 ? message.substring(0, 200) + "..." : message;
        }
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            detail
        );
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TYPE));
        problemDetail.setTitle("Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles all other exceptions (catch-all handler).
     * 
     * <p>This handler catches any unhandled exceptions and returns a generic
     * 500 INTERNAL SERVER ERROR response. In production, consider logging
     * the exception details without exposing them to the client.
     * 
     * @param ex the exception
     * @return ResponseEntity with 500 INTERNAL SERVER ERROR and ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(final Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred"
        );
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TYPE));
        problemDetail.setTitle("Internal Server Error");
        
        // Log exception details for debugging (without exposing to client)
        System.err.println("=== EXCEPTION CAUGHT ===");
        System.err.println("Exception type: " + ex.getClass().getName());
        System.err.println("Exception message: " + ex.getMessage());
        ex.printStackTrace(System.err);
        if (ex.getCause() != null) {
            System.err.println("Caused by: " + ex.getCause().getClass().getName());
            System.err.println("Caused by message: " + ex.getCause().getMessage());
            ex.getCause().printStackTrace(System.err);
        }
        System.err.println("========================");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}

