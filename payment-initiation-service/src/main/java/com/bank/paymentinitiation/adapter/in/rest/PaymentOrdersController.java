package com.bank.paymentinitiation.adapter.in.rest;

import com.bank.paymentinitiation.adapter.in.rest.mapper.PaymentOrderRestMapper;
import com.bank.paymentinitiation.application.service.PaymentOrderReferenceGenerator;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.generated.api.PaymentOrdersApi;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Controlador REST que implementa PaymentOrdersApi (generada por OpenAPI).
 * 
 * <p>Este controlador:
 * <ul>
 *   <li>Recibe requests HTTP y los mapea a casos de uso del dominio</li>
 *   <li>Genera referencias de órdenes de pago</li>
 *   <li>Mapea entre DTOs REST y modelo de dominio</li>
 *   <li>Retorna respuestas HTTP apropiadas</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
public class PaymentOrdersController implements PaymentOrdersApi {

    private final InitiatePaymentOrderUseCase initiatePaymentOrderUseCase;
    private final RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;
    private final PaymentOrderRestMapper mapper;
    private final PaymentOrderReferenceGenerator referenceGenerator;

    @Override
    public ResponseEntity<InitiatePaymentOrderResponse> initiatePaymentOrder(
            @Valid final InitiatePaymentOrderRequest request) {
        // Generar paymentOrderReference
        String paymentOrderReference = referenceGenerator.generate();

        // Mapear DTO → dominio
        PaymentOrder domainOrder = mapper.toDomain(request, paymentOrderReference);

        // Llamar al caso de uso
        PaymentOrder initiatedOrder = initiatePaymentOrderUseCase.initiate(domainOrder);

        // Mapear dominio → DTO de respuesta
        InitiatePaymentOrderResponse response = mapper.toInitiateResponse(initiatedOrder);

        // Retornar respuesta HTTP 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<RetrievePaymentOrderResponse> retrievePaymentOrder(
            final String paymentOrderId) {
        // Llamar al caso de uso
        PaymentOrder domainOrder = retrievePaymentOrderUseCase.retrieve(paymentOrderId);

        // Mapear dominio → DTO de respuesta
        RetrievePaymentOrderResponse response = mapper.toRetrieveResponse(domainOrder);

        // Retornar respuesta HTTP 200 OK
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(
            final String paymentOrderId) {
        // Llamar al caso de uso (necesita orden completa para mapear paymentOrderReference y lastUpdated)
        PaymentOrder domainOrder = retrievePaymentOrderUseCase.retrieve(paymentOrderId);

        // Mapear dominio → DTO de respuesta
        PaymentOrderStatusResponse response = mapper.toStatusResponse(domainOrder);

        // Retornar respuesta HTTP 200 OK
        return ResponseEntity.ok(response);
    }
}

