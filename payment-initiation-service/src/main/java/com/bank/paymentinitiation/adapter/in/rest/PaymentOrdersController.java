package com.bank.paymentinitiation.adapter.in.rest;

import com.bank.paymentinitiation.adapter.in.rest.mapper.PaymentOrderRestMapper;
import com.bank.paymentinitiation.application.service.InitiatePaymentOrderService;
import com.bank.paymentinitiation.application.service.PaymentOrderReferenceGenerator;
import com.bank.paymentinitiation.application.service.RetrievePaymentOrderService;
import com.bank.paymentinitiation.application.service.RetrievePaymentOrderStatusService;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.bank.paymentinitiation.generated.api.PaymentOrdersApi;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Controlador REST que implementa la API de Payment Orders.
 * 
 * Este controlador implementa PaymentOrdersApi (generada por OpenAPI Generator)
 * y actúa como adaptador de entrada (inbound) en la arquitectura hexagonal.
 * 
 * Responsabilidades:
 * - Recibir peticiones HTTP REST
 * - Validar DTOs de entrada
 * - Mapear DTOs REST ↔ Dominio
 * - Invocar casos de uso
 * - Mapear Dominio → DTOs REST de respuesta
 * - Devolver respuestas HTTP apropiadas
 * 
 * Este controlador NO debe contener lógica de negocio, solo orquestar
 * las llamadas a los casos de uso y manejar la conversión entre capas.
 * 
 * @author Payment Initiation Service Team
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentOrdersController implements PaymentOrdersApi {
    
    private final InitiatePaymentOrderUseCase initiateUseCase;
    private final RetrievePaymentOrderUseCase retrieveUseCase;
    private final RetrievePaymentOrderStatusUseCase retrieveStatusUseCase;
    private final PaymentOrderRestMapper restMapper;
    private final PaymentOrderReferenceGenerator referenceGenerator;
    
    @Override
    public ResponseEntity<InitiatePaymentOrderResponse> initiatePaymentOrder(
            @Valid InitiatePaymentOrderRequest request) {
        
        log.info("Initiating payment order for external reference: {}", request.getExternalReference());
        
        // Generar referencia única
        String paymentOrderReference = referenceGenerator.generate();
        
        // Mapear DTO → Dominio
        var domainOrder = restMapper.toDomain(request, paymentOrderReference);
        
        // Invocar caso de uso
        var initiatedOrder = initiateUseCase.initiate(domainOrder);
        
        // Mapear Dominio → DTO de respuesta
        var response = restMapper.toInitiateResponse(initiatedOrder);
        
        log.info("Payment order initiated successfully: {}", response.getPaymentOrderReference());
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }
    
    @Override
    public ResponseEntity<RetrievePaymentOrderResponse> retrievePaymentOrder(String id) {
        
        log.info("Retrieving payment order: {}", id);
        
        // Invocar caso de uso
        var paymentOrder = retrieveUseCase.retrieve(id);
        
        // Mapear Dominio → DTO de respuesta
        var response = restMapper.toRetrieveResponse(paymentOrder);
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(String id) {
        
        log.info("Retrieving payment order status: {}", id);
        
        // Invocar caso de uso para obtener la orden completa (necesaria para mapear a response)
        // Alternativamente, RetrievePaymentOrderStatusUseCase podría retornar solo el status
        // pero necesitamos paymentOrderReference y lastUpdated, así que usamos retrieveUseCase
        var paymentOrder = retrieveUseCase.retrieve(id);
        
        // Mapear Dominio → DTO de respuesta
        var response = restMapper.toStatusResponse(paymentOrder);
        
        return ResponseEntity.ok(response);
    }
}

