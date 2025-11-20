package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.Account;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatus;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * MapStruct mapper for converting between REST DTOs and domain models.
 * 
 * This mapper handles the transformation between:
 * - OpenAPI generated DTOs (in generated.model package)
 * - Domain models (in domain.model package)
 * 
 * Responsibilities:
 * - Convert InitiatePaymentOrderRequest → PaymentOrder (domain)
 * - Convert PaymentOrder (domain) → InitiatePaymentOrderResponse
 * - Convert PaymentOrder (domain) → RetrievePaymentOrderResponse
 * - Convert PaymentOrder (domain) → PaymentOrderStatusResponse
 * 
 * This mapper will be implemented by MapStruct at compile time.
 * 
 * Note: Uses fully qualified names to avoid ambiguity between domain and generated types.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentOrderRestMapper {
    
    /**
     * Converts an InitiatePaymentOrderRequest to a domain PaymentOrder.
     * 
     * The paymentOrderReference is passed as a parameter because it is generated
     * in the controller, not in the mapper.
     * 
     * @param request the REST request DTO
     * @param paymentOrderReference the payment order reference (generated in controller)
     * @return the domain PaymentOrder
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "externalReference", source = "request.externalReference", qualifiedByName = "stringToExternalReference")
    @Mapping(target = "payerReference", source = "request.debtorAccount.iban", qualifiedByName = "ibanToPayerReference")
    @Mapping(target = "payeeReference", source = "request.creditorAccount.iban", qualifiedByName = "ibanToPayeeReference")
    @Mapping(target = "instructedAmount", source = "request.instructedAmount", qualifiedByName = "generatedPaymentAmountToDomain")
    @Mapping(target = "remittanceInformation", source = "request.remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "request.requestedExecutionDate")
    @Mapping(target = "status", ignore = true) // Will be set by domain logic
    @Mapping(target = "createdAt", ignore = true) // Will be set by domain logic
    @Mapping(target = "updatedAt", ignore = true) // Will be set by domain logic
    PaymentOrder toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference);
    
    /**
     * Converts a domain PaymentOrder to an InitiatePaymentOrderResponse.
     * 
     * @param paymentOrder the domain PaymentOrder
     * @return the REST response DTO
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "paymentOrderStatus", source = "status", qualifiedByName = "domainStatusToGenerated")
    @Mapping(target = "debtorAccount", source = "payerReference", qualifiedByName = "payerReferenceToAccount")
    @Mapping(target = "creditorAccount", source = "payeeReference", qualifiedByName = "payeeReferenceToAccount")
    @Mapping(target = "instructedAmount", source = "instructedAmount", qualifiedByName = "domainPaymentAmountToGenerated")
    @Mapping(target = "remittanceInformation", source = "remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    InitiatePaymentOrderResponse toInitiateResponse(PaymentOrder paymentOrder);
    
    /**
     * Converts a domain PaymentOrder to a RetrievePaymentOrderResponse.
     * 
     * @param paymentOrder the domain PaymentOrder
     * @return the REST response DTO
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "paymentOrderStatus", source = "status", qualifiedByName = "domainStatusToGenerated")
    @Mapping(target = "externalReference", source = "externalReference.value")
    @Mapping(target = "debtorAccount", source = "payerReference", qualifiedByName = "payerReferenceToAccount")
    @Mapping(target = "creditorAccount", source = "payeeReference", qualifiedByName = "payeeReferenceToAccount")
    @Mapping(target = "instructedAmount", source = "instructedAmount", qualifiedByName = "domainPaymentAmountToGenerated")
    @Mapping(target = "remittanceInformation", source = "remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "lastUpdated", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    RetrievePaymentOrderResponse toRetrieveResponse(PaymentOrder paymentOrder);
    
    /**
     * Converts a domain PaymentOrder to a PaymentOrderStatusResponse.
     * 
     * @param paymentOrder the domain PaymentOrder
     * @return the REST response DTO
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "paymentOrderStatus", source = "status", qualifiedByName = "domainStatusToGenerated")
    @Mapping(target = "lastUpdated", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    PaymentOrderStatusResponse toStatusResponse(PaymentOrder paymentOrder);
    
    // Named methods for custom conversions
    
    @Named("stringToExternalReference")
    default ExternalReference stringToExternalReference(String value) {
        return value != null ? ExternalReference.of(value) : null;
    }
    
    @Named("ibanToPayerReference")
    default PayerReference ibanToPayerReference(String iban) {
        return iban != null ? PayerReference.of(iban) : null;
    }
    
    @Named("ibanToPayeeReference")
    default PayeeReference ibanToPayeeReference(String iban) {
        return iban != null ? PayeeReference.of(iban) : null;
    }
    
    @Named("payerReferenceToAccount")
    default Account payerReferenceToAccount(PayerReference payerReference) {
        if (payerReference == null) {
            return null;
        }
        Account account = new Account();
        account.setIban(payerReference.getValue());
        return account;
    }
    
    @Named("payeeReferenceToAccount")
    default Account payeeReferenceToAccount(PayeeReference payeeReference) {
        if (payeeReference == null) {
            return null;
        }
        Account account = new Account();
        account.setIban(payeeReference.getValue());
        return account;
    }
    
    @Named("generatedPaymentAmountToDomain")
    default PaymentAmount generatedPaymentAmountToDomain(
            com.bank.paymentinitiation.generated.model.PaymentAmount generatedAmount) {
        if (generatedAmount == null || generatedAmount.getAmount() == null || generatedAmount.getCurrency() == null) {
            return null;
        }
        return PaymentAmount.of(
            BigDecimal.valueOf(generatedAmount.getAmount()),
            generatedAmount.getCurrency().getValue()
        );
    }
    
    @Named("domainPaymentAmountToGenerated")
    default com.bank.paymentinitiation.generated.model.PaymentAmount domainPaymentAmountToGenerated(
            PaymentAmount domainAmount) {
        if (domainAmount == null) {
            return null;
        }
        com.bank.paymentinitiation.generated.model.PaymentAmount generatedAmount = 
            new com.bank.paymentinitiation.generated.model.PaymentAmount();
        generatedAmount.setAmount(domainAmount.getValue().doubleValue());
        generatedAmount.setCurrency(
            com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum.fromValue(domainAmount.getCurrency())
        );
        return generatedAmount;
    }
    
    @Named("domainStatusToGenerated")
    default PaymentOrderStatus domainStatusToGenerated(PaymentStatus domainStatus) {
        if (domainStatus == null) {
            return null;
        }
        return PaymentOrderStatus.fromValue(domainStatus.name());
    }
    
    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
}
