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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Payment Orders.
 * 
 * <p>This controller implements the {@link PaymentOrdersApi} interface generated
 * from the OpenAPI specification. It handles HTTP requests and delegates business
 * logic to application services.
 * 
 * <p>This adapter follows the Hexagonal Architecture pattern:
 * <ul>
 *   <li>Implements the generated API interface (contract-first approach)</li>
 *   <li>Delegates to application services via input ports (use cases)</li>
 *   <li>Converts between REST DTOs and domain objects using mappers</li>
 *   <li>Handles HTTP-specific concerns (status codes, headers, etc.)</li>
 * </ul>
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Receive HTTP requests</li>
 *   <li>Validate request parameters and body (via Bean Validation)</li>
 *   <li>Map REST DTOs to domain objects using mapper</li>
 *   <li>Call application services (use cases)</li>
 *   <li>Map domain objects to REST DTOs using mapper</li>
 *   <li>Return appropriate HTTP responses</li>
 *   <li>Handle exceptions and convert to ProblemDetail responses (via GlobalExceptionHandler)</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
public class PaymentOrdersRestController implements PaymentOrdersApi {

    private final InitiatePaymentOrderUseCase initiatePaymentOrderUseCase;
    private final RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;
    private final PaymentOrderRestMapper paymentOrderRestMapper;
    private final PaymentOrderReferenceGenerator paymentOrderReferenceGenerator;

    /**
     * {@inheritDoc}
     * 
     * <p>Process flow:
     * <ol>
     *   <li>Validate request using @Valid (Bean Validation)</li>
     *   <li>Generate paymentOrderReference using PaymentOrderReferenceGenerator</li>
     *   <li>Map InitiatePaymentOrderRequest to PaymentOrder domain object</li>
     *   <li>Call InitiatePaymentOrderUseCase to initiate the order</li>
     *   <li>Map PaymentOrder domain object to InitiatePaymentOrderResponse</li>
     *   <li>Return ResponseEntity with 201 CREATED status</li>
     * </ol>
     */
    @Override
    public ResponseEntity<InitiatePaymentOrderResponse> initiatePaymentOrder(
            @Valid final InitiatePaymentOrderRequest initiatePaymentOrderRequest) {
        
        // Generate payment order reference
        String paymentOrderReference = paymentOrderReferenceGenerator.generate();
        
        // Map REST DTO to domain object
        PaymentOrder paymentOrder = paymentOrderRestMapper.toDomain(
            initiatePaymentOrderRequest, paymentOrderReference);
        
        // Call use case to initiate payment order
        PaymentOrder initiatedOrder = initiatePaymentOrderUseCase.initiate(paymentOrder);
        
        // Map domain object to REST response DTO
        InitiatePaymentOrderResponse response = paymentOrderRestMapper.toInitiateResponse(initiatedOrder);
        
        // Return 201 CREATED with response body
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Process flow:
     * <ol>
     *   <li>Call RetrievePaymentOrderUseCase to retrieve the order</li>
     *   <li>Map PaymentOrder domain object to RetrievePaymentOrderResponse</li>
     *   <li>Return ResponseEntity with 200 OK status</li>
     *   <li>If order not found, RetrievePaymentOrderUseCase throws PaymentOrderNotFoundException</li>
     *   <li>Exception is handled by GlobalExceptionHandler and converted to 404 response</li>
     * </ol>
     */
    @Override
    public ResponseEntity<RetrievePaymentOrderResponse> retrievePaymentOrder(final String paymentOrderId) {
        // Call use case to retrieve payment order
        PaymentOrder paymentOrder = retrievePaymentOrderUseCase.retrieve(paymentOrderId);
        
        // Map domain object to REST response DTO
        RetrievePaymentOrderResponse response = paymentOrderRestMapper.toRetrieveResponse(paymentOrder);
        
        // Return 200 OK with response body
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Process flow:
     * <ol>
     *   <li>Call RetrievePaymentOrderUseCase to retrieve the complete order</li>
     *   <li>Map PaymentOrder domain object to PaymentOrderStatusResponse</li>
     *   <li>Return ResponseEntity with 200 OK status</li>
     *   <li>If order not found, RetrievePaymentOrderUseCase throws PaymentOrderNotFoundException</li>
     *   <li>Exception is handled by GlobalExceptionHandler and converted to 404 response</li>
     * </ol>
     * 
     * <p>Note: Although RetrievePaymentOrderStatusUseCase exists and returns only PaymentStatus,
     * this controller uses RetrievePaymentOrderUseCase because it needs the complete order
     * to include paymentOrderReference and lastUpdate in the response.
     */
    @Override
    public ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(final String paymentOrderId) {
        // Call use case to retrieve payment order (need complete order for lastUpdate)
        PaymentOrder paymentOrder = retrievePaymentOrderUseCase.retrieve(paymentOrderId);
        
        // Map domain object to REST status response DTO
        PaymentOrderStatusResponse response = paymentOrderRestMapper.toStatusResponse(paymentOrder);
        
        // Return 200 OK with response body
        return ResponseEntity.ok(response);
    }
}

