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

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para el controlador REST.
 * 
 * <p>Este handler mapea excepciones de dominio y de Spring a respuestas HTTP
 * siguiendo el estándar RFC 7807 (Problem Details for HTTP APIs).
 * 
 * <p>Todas las respuestas de error usan content-type `application/problem+json`.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja PaymentOrderNotFoundException (orden de pago no encontrada).
     *
     * @param ex la excepción
     * @return ResponseEntity con ProblemDetail y status 404 NOT FOUND
     */
    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePaymentOrderNotFoundException(
            final PaymentOrderNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Payment Order Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Maneja InvalidPaymentException (orden de pago inválida).
     *
     * @param ex la excepción
     * @return ResponseEntity con ProblemDetail y status 400 BAD REQUEST
     */
    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPaymentException(
            final InvalidPaymentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Payment Order");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Maneja MethodArgumentNotValidException (validación de @Valid fallida).
     * 
     * <p>Esta excepción se lanza cuando `@Valid` falla en los parámetros del controlador.
     * Incluye detalles de los campos que fallaron la validación.
     *
     * @param ex la excepción
     * @return ResponseEntity con ProblemDetail y status 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, detail.isEmpty() ? "Validation failed" : detail);
        problemDetail.setTitle("Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Maneja HttpMessageNotReadableException (JSON malformado o no parseable).
     * 
     * <p>Esta excepción se lanza cuando el JSON del request body no puede ser parseado
     * (ej: fecha en formato incorrecto, JSON sintácticamente inválido).
     *
     * @param ex la excepción
     * @return ResponseEntity con ProblemDetail y status 400 BAD REQUEST
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex) {
        String detail = ex.getMessage();
        if (detail != null && detail.contains("JSON parse error")) {
            detail = "Invalid JSON format: " + detail;
        } else if (detail != null && detail.contains("Cannot deserialize")) {
            detail = "Invalid request format: " + detail;
        } else {
            detail = "Request body is not readable or has invalid format";
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle("Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Maneja excepciones genéricas no manejadas (errores inesperados).
     *
     * @param ex la excepción
     * @return ResponseEntity con ProblemDetail y status 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(final Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + ex.getMessage());
        problemDetail.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}

