package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.CreditorAccount;
import com.bank.paymentinitiation.generated.model.DebtorAccount;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper MapStruct para convertir entre DTOs REST (generados por OpenAPI) y PaymentOrder (dominio).
 * 
 * <p>Este mapper maneja la conversión entre:
 * <ul>
 *   <li>DTOs generados ↔ Modelo de dominio</li>
 *   <li>PaymentStatus (generado) ↔ PaymentStatus (dominio)</li>
 *   <li>PaymentAmount (generado) ↔ PaymentAmount (dominio)</li>
 *   <li>Account objects ↔ Reference value objects</li>
 *   <li>LocalDateTime ↔ OffsetDateTime</li>
 * </ul>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentOrderRestMapper {

    /**
     * Convierte InitiatePaymentOrderRequest a PaymentOrder del dominio.
     * 
     * <p>El paymentOrderReference se pasa como parámetro adicional porque
     * se genera en el controlador, no en el mapper.
     *
     * @param request el DTO de request
     * @param paymentOrderReference la referencia de la orden (generada en el controlador)
     * @return el PaymentOrder del dominio
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(new com.bank.paymentinitiation.domain.model.ExternalReference(request.getExternalReference()))")
    @Mapping(target = "payerReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayerReference(request.getDebtorAccount().getIban()))")
    @Mapping(target = "payeeReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayeeReference(request.getCreditorAccount().getIban()))")
    @Mapping(target = "instructedAmount", expression = "java(com.bank.paymentinitiation.domain.model.PaymentAmount.of(request.getInstructedAmount().getAmount(), request.getInstructedAmount().getCurrency().getValue()))")
    @Mapping(target = "remittanceInformation", source = "request.remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "request.requestedExecutionDate")
    @Mapping(target = "status", ignore = true) // Se establece en initiate()
    @Mapping(target = "createdAt", ignore = true) // Se establece en initiate()
    @Mapping(target = "updatedAt", ignore = true) // Se establece en initiate()
    PaymentOrder toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference);

    /**
     * Convierte PaymentOrder del dominio a InitiatePaymentOrderResponse.
     *
     * @param domain el PaymentOrder del dominio
     * @return el DTO de response
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderReference")
    @Mapping(target = "status", expression = "java(com.bank.paymentinitiation.generated.model.PaymentStatus.fromValue(domain.getStatus().name()))")
    InitiatePaymentOrderResponse toInitiateResponse(PaymentOrder domain);

    /**
     * Convierte PaymentOrder del dominio a RetrievePaymentOrderResponse.
     *
     * @param domain el PaymentOrder del dominio
     * @return el DTO de response
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(domain.getExternalReference().getValue())")
    @Mapping(target = "debtorAccount", expression = "java(new com.bank.paymentinitiation.generated.model.DebtorAccount(domain.getPayerReference().getValue()))")
    @Mapping(target = "creditorAccount", expression = "java(new com.bank.paymentinitiation.generated.model.CreditorAccount(domain.getPayeeReference().getValue()))")
    @Mapping(target = "instructedAmount", expression = "java(new com.bank.paymentinitiation.generated.model.PaymentAmount(domain.getInstructedAmount().getValue(), com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum.fromValue(domain.getInstructedAmount().getCurrency())))")
    @Mapping(target = "remittanceInformation", source = "remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate")
    @Mapping(target = "status", expression = "java(com.bank.paymentinitiation.generated.model.PaymentStatus.fromValue(domain.getStatus().name()))")
    @Mapping(target = "lastUpdate", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    RetrievePaymentOrderResponse toRetrieveResponse(PaymentOrder domain);

    /**
     * Convierte PaymentOrder del dominio a PaymentOrderStatusResponse.
     *
     * @param domain el PaymentOrder del dominio
     * @return el DTO de response
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderReference")
    @Mapping(target = "status", expression = "java(com.bank.paymentinitiation.generated.model.PaymentStatus.fromValue(domain.getStatus().name()))")
    @Mapping(target = "lastUpdate", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    PaymentOrderStatusResponse toStatusResponse(PaymentOrder domain);

    /**
     * Convierte LocalDateTime a OffsetDateTime usando UTC como zona horaria.
     *
     * @param localDateTime el LocalDateTime a convertir
     * @return el OffsetDateTime equivalente
     */
    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}

