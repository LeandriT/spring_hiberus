package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentAccount;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatus;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper between REST DTOs (generated from OpenAPI) and Domain Objects.
 * 
 * <p>This mapper handles conversion between:
 * <ul>
 *   <li>REST DTOs (InitiatePaymentOrderRequest, InitiatePaymentOrderResponse, etc.)</li>
 *   <li>Domain Objects (PaymentOrder, PaymentStatus, PaymentAmount, etc.)</li>
 * </ul>
 * 
 * <p>Note: Uses fully qualified names for types to avoid ambiguity between
 * domain model types and generated model types (e.g., PaymentAmount, PaymentStatus).
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentOrderRestMapper {

    /**
     * Maps InitiatePaymentOrderRequest (REST DTO) to PaymentOrder (Domain).
     * 
     * <p>Note: paymentOrderReference is passed as a separate parameter because
     * it is generated in the controller/service, not in the mapper.
     * 
     * @param request the REST request DTO
     * @param paymentOrderReference the generated payment order reference
     * @return the domain PaymentOrder object
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(new com.bank.paymentinitiation.domain.model.ExternalReference(request.getExternalReference()))")
    @Mapping(target = "payerReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayerReference(request.getDebtorAccount().getIban()))")
    @Mapping(target = "payeeReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayeeReference(request.getCreditorAccount().getIban()))")
    @Mapping(target = "instructedAmount", expression = "java(com.bank.paymentinitiation.domain.model.PaymentAmount.of(request.getInstructedAmount().getAmount(), request.getInstructedAmount().getCurrency().getValue()))")
    @Mapping(target = "remittanceInformation", source = "request.remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "request.requestedExecutionDate")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentOrder toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference);

    /**
     * Maps PaymentOrder (Domain) to InitiatePaymentOrderResponse (REST DTO).
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderReference")
    @Mapping(target = "status", expression = "java(toPaymentOrderStatus(domain.getStatus()))")
    InitiatePaymentOrderResponse toInitiateResponse(PaymentOrder domain);

    /**
     * Maps PaymentOrder (Domain) to RetrievePaymentOrderResponse (REST DTO).
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(domain.getExternalReference().getValue())")
    @Mapping(target = "debtorAccount", expression = "java(toPaymentAccount(domain.getPayerReference().getValue()))")
    @Mapping(target = "creditorAccount", expression = "java(toPaymentAccount(domain.getPayeeReference().getValue()))")
    @Mapping(target = "instructedAmount", expression = "java(toGeneratedPaymentAmount(domain.getInstructedAmount()))")
    @Mapping(target = "remittanceInformation", source = "remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate")
    @Mapping(target = "status", expression = "java(toPaymentOrderStatus(domain.getStatus()))")
    @Mapping(target = "lastUpdate", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    RetrievePaymentOrderResponse toRetrieveResponse(PaymentOrder domain);

    /**
     * Maps PaymentOrder (Domain) to PaymentOrderStatusResponse (REST DTO).
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderReference")
    @Mapping(target = "status", expression = "java(toPaymentOrderStatus(domain.getStatus()))")
    @Mapping(target = "lastUpdate", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    PaymentOrderStatusResponse toStatusResponse(PaymentOrder domain);

    // Helper methods for conversions

    /**
     * Converts domain PaymentStatus to generated PaymentOrderStatus enum.
     */
    @Named("toPaymentOrderStatus")
    default PaymentOrderStatus toPaymentOrderStatus(final PaymentStatus domainStatus) {
        if (domainStatus == null) {
            return null;
        }
        // Map domain status to OpenAPI status
        // Domain: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED
        // OpenAPI: ACCEPTED, PENDING, PROCESSING, SETTLED, REJECTED, FAILED, CANCELLED
        return switch (domainStatus) {
            case INITIATED -> PaymentOrderStatus.ACCEPTED;
            case PENDING -> PaymentOrderStatus.PENDING;
            case PROCESSED -> PaymentOrderStatus.PROCESSING;
            case COMPLETED -> PaymentOrderStatus.SETTLED;
            case FAILED -> PaymentOrderStatus.FAILED;
            case CANCELLED -> PaymentOrderStatus.CANCELLED;
        };
    }

    /**
     * Converts LocalDateTime to OffsetDateTime (for REST DTOs).
     */
    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(final LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    /**
     * Creates a PaymentAccount (generated model) from an IBAN string.
     */
    default PaymentAccount toPaymentAccount(final String iban) {
        if (iban == null) {
            return null;
        }
        PaymentAccount account = new PaymentAccount();
        account.setIban(iban);
        return account;
    }

    /**
     * Converts domain PaymentAmount to generated PaymentAmount.
     */
    default com.bank.paymentinitiation.generated.model.PaymentAmount toGeneratedPaymentAmount(
            final PaymentAmount domainAmount) {
        if (domainAmount == null) {
            return null;
        }
        com.bank.paymentinitiation.generated.model.PaymentAmount generatedAmount =
            new com.bank.paymentinitiation.generated.model.PaymentAmount();
        generatedAmount.setAmount(domainAmount.getValue());
        // Convert String currency to PaymentAmount.CurrencyEnum
        com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum currencyEnum =
            com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum.fromValue(domainAmount.getCurrency());
        generatedAmount.setCurrency(currencyEnum);
        return generatedAmount;
    }
}

