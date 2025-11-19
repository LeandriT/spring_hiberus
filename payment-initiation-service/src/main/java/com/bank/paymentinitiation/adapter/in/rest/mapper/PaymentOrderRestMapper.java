package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.*;
import com.bank.paymentinitiation.generated.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct para convertir entre DTOs REST (generados de OpenAPI) y objetos de dominio.
 * 
 * Este mapper es responsable de transformar:
 * - InitiatePaymentOrderRequest (DTO) → PaymentOrder (Dominio)
 * - PaymentOrder (Dominio) → InitiatePaymentOrderResponse (DTO)
 * - PaymentOrder (Dominio) → RetrievePaymentOrderResponse (DTO)
 * - PaymentOrder (Dominio) → PaymentOrderStatusResponse (DTO)
 * 
 * Los DTOs provienen del paquete com.bank.paymentinitiation.generated.model.
 * 
 * Configuración:
 * - componentModel = "spring": Genera un componente Spring inyectable
 * - unmappedTargetPolicy = ReportingPolicy.ERROR: Falla si hay campos sin mapear
 * 
 * @author Payment Initiation Service Team
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentOrderRestMapper {
    
    /**
     * Convierte InitiatePaymentOrderRequest a PaymentOrder del dominio.
     * 
     * Nota: Este método NO crea el PaymentOrder directamente porque usa el método estático create().
     * En su lugar, prepara los datos y el caso de uso debe llamar a PaymentOrder.create()
     * con un paymentOrderReference generado.
     * 
     * @param request El DTO de request de OpenAPI
     * @param paymentOrderReference La referencia de la orden generada por el caso de uso
     * @return Un objeto PaymentOrder del dominio creado con PaymentOrder.create()
     */
    default com.bank.paymentinitiation.domain.model.PaymentOrder toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference) {
        if (request == null) {
            return null;
        }
        
        com.bank.paymentinitiation.domain.model.ExternalReference externalRef = 
            stringToExternalReference(request.getExternalReference());
        com.bank.paymentinitiation.domain.model.PayerReference payerRef = 
            ibanToPayerReference(request.getDebtorAccount() != null ? request.getDebtorAccount().getIban() : null);
        com.bank.paymentinitiation.domain.model.PayeeReference payeeRef = 
            ibanToPayeeReference(request.getCreditorAccount() != null ? request.getCreditorAccount().getIban() : null);
        com.bank.paymentinitiation.domain.model.PaymentAmount amount = 
            dtoToDomainPaymentAmount(request.getInstructedAmount());
        
        return com.bank.paymentinitiation.domain.model.PaymentOrder.create(
            paymentOrderReference,
            externalRef,
            payerRef,
            payeeRef,
            amount,
            request.getRemittanceInformation(),
            request.getRequestedExecutionDate()
        );
    }
    
    /**
     * Convierte PaymentOrder del dominio a InitiatePaymentOrderResponse.
     */
    @Mapping(target = "externalReference", source = "externalReference.value")
    @Mapping(target = "debtorAccount", source = "payerReference", qualifiedByName = "payerReferenceToAccount")
    @Mapping(target = "creditorAccount", source = "payeeReference", qualifiedByName = "payeeReferenceToAccount")
    @Mapping(target = "instructedAmount", source = "instructedAmount", qualifiedByName = "domainToDtoPaymentAmount")
    @Mapping(target = "status", source = "status", qualifiedByName = "domainToDtoPaymentStatus")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    InitiatePaymentOrderResponse toInitiateResponse(com.bank.paymentinitiation.domain.model.PaymentOrder domain);
    
    /**
     * Convierte PaymentOrder del dominio a RetrievePaymentOrderResponse.
     */
    @Mapping(target = "externalReference", source = "externalReference.value")
    @Mapping(target = "debtorAccount", source = "payerReference", qualifiedByName = "payerReferenceToAccount")
    @Mapping(target = "creditorAccount", source = "payeeReference", qualifiedByName = "payeeReferenceToAccount")
    @Mapping(target = "instructedAmount", source = "instructedAmount", qualifiedByName = "domainToDtoPaymentAmount")
    @Mapping(target = "status", source = "status", qualifiedByName = "domainToDtoPaymentStatus")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    RetrievePaymentOrderResponse toRetrieveResponse(com.bank.paymentinitiation.domain.model.PaymentOrder domain);
    
    /**
     * Convierte PaymentOrder del dominio a PaymentOrderStatusResponse.
     */
    @Mapping(target = "paymentOrderStatus", source = "status", qualifiedByName = "domainToDtoPaymentStatus")
    @Mapping(target = "lastUpdated", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    PaymentOrderStatusResponse toStatusResponse(com.bank.paymentinitiation.domain.model.PaymentOrder domain);
    
    // Métodos de mapeo personalizados
    
    @Named("stringToExternalReference")
    default com.bank.paymentinitiation.domain.model.ExternalReference stringToExternalReference(String value) {
        return value != null ? com.bank.paymentinitiation.domain.model.ExternalReference.of(value) : null;
    }
    
    @Named("ibanToPayerReference")
    default com.bank.paymentinitiation.domain.model.PayerReference ibanToPayerReference(String iban) {
        return iban != null ? com.bank.paymentinitiation.domain.model.PayerReference.of(iban) : null;
    }
    
    @Named("ibanToPayeeReference")
    default com.bank.paymentinitiation.domain.model.PayeeReference ibanToPayeeReference(String iban) {
        return iban != null ? com.bank.paymentinitiation.domain.model.PayeeReference.of(iban) : null;
    }
    
    @Named("dtoToDomainPaymentAmount")
    default com.bank.paymentinitiation.domain.model.PaymentAmount dtoToDomainPaymentAmount(com.bank.paymentinitiation.generated.model.PaymentAmount dto) {
        if (dto == null || dto.getAmount() == null || dto.getCurrency() == null) {
            return null;
        }
        return com.bank.paymentinitiation.domain.model.PaymentAmount.of(dto.getAmount(), dto.getCurrency());
    }
    
    @Named("domainToDtoPaymentAmount")
    default com.bank.paymentinitiation.generated.model.PaymentAmount domainToDtoPaymentAmount(com.bank.paymentinitiation.domain.model.PaymentAmount domain) {
        if (domain == null) {
            return null;
        }
        return new com.bank.paymentinitiation.generated.model.PaymentAmount(
            domain.getValue(),
            domain.getCurrency()
        );
    }
    
    @Named("payerReferenceToAccount")
    default Account payerReferenceToAccount(com.bank.paymentinitiation.domain.model.PayerReference payerReference) {
        if (payerReference == null) {
            return null;
        }
        return new Account(payerReference.getValue());
    }
    
    @Named("payeeReferenceToAccount")
    default Account payeeReferenceToAccount(com.bank.paymentinitiation.domain.model.PayeeReference payeeReference) {
        if (payeeReference == null) {
            return null;
        }
        return new Account(payeeReference.getValue());
    }
    
    @Named("domainToDtoPaymentStatus")
    default com.bank.paymentinitiation.generated.model.PaymentStatus domainToDtoPaymentStatus(com.bank.paymentinitiation.domain.model.PaymentStatus domain) {
        if (domain == null) {
            return null;
        }
        return com.bank.paymentinitiation.generated.model.PaymentStatus.fromValue(domain.name());
    }
    
    @Named("localDateTimeToOffsetDateTime")
    default java.time.OffsetDateTime localDateTimeToOffsetDateTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(java.time.ZoneOffset.UTC);
    }
}

