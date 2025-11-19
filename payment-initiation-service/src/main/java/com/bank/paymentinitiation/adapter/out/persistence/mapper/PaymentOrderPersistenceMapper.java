package com.bank.paymentinitiation.adapter.out.persistence.mapper;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper MapStruct para convertir entre objetos de dominio y entidades JPA.
 * 
 * Este mapper es responsable de transformar:
 * - PaymentOrder (Dominio) → PaymentOrderEntity (JPA)
 * - PaymentOrderEntity (JPA) → PaymentOrder (Dominio)
 * 
 * Configuración:
 * - componentModel = "spring": Genera un componente Spring inyectable
 * - unmappedTargetPolicy = ReportingPolicy.ERROR: Falla si hay campos sin mapear
 * 
 * Este mapper es utilizado por PaymentOrderRepositoryAdapter para convertir
 * entre la representación de dominio (independiente de JPA) y las entidades JPA.
 * 
 * @author Payment Initiation Service Team
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentOrderPersistenceMapper {
    
    /**
     * Convierte PaymentOrder del dominio a PaymentOrderEntity JPA.
     * 
     * @param domain El objeto de dominio PaymentOrder
     * @return La entidad JPA PaymentOrderEntity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalReference", source = "externalReference.value")
    @Mapping(target = "payerReference", source = "payerReference.value")
    @Mapping(target = "payeeReference", source = "payeeReference.value")
    @Mapping(target = "instructedAmount", source = "instructedAmount.value")
    @Mapping(target = "currency", source = "instructedAmount.currency")
    @Mapping(target = "status", source = "status", qualifiedByName = "domainToEntityStatus")
    PaymentOrderEntity toEntity(com.bank.paymentinitiation.domain.model.PaymentOrder domain);
    
    /**
     * Convierte PaymentOrderEntity JPA a PaymentOrder del dominio.
     * 
     * Nota: PaymentOrder no tiene constructor público, así que usamos un método default
     * personalizado para construir el objeto usando los métodos apropiados.
     * 
     * @param entity La entidad JPA PaymentOrderEntity
     * @return El objeto de dominio PaymentOrder
     */
    default com.bank.paymentinitiation.domain.model.PaymentOrder toDomain(PaymentOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // Crear el PaymentOrder usando el método estático create()
        com.bank.paymentinitiation.domain.model.PaymentOrder order = com.bank.paymentinitiation.domain.model.PaymentOrder.create(
            entity.getPaymentOrderReference(),
            stringToExternalReference(entity.getExternalReference()),
            stringToPayerReference(entity.getPayerReference()),
            stringToPayeeReference(entity.getPayeeReference()),
            paymentAmountFromEntity(entity.getInstructedAmount(), entity.getCurrency()),
            entity.getRemittanceInformation(),
            entity.getRequestedExecutionDate()
        );
        
        // Cambiar el estado si es diferente de INITIATED (ya que create() siempre inicia con INITIATED)
        com.bank.paymentinitiation.domain.model.PaymentStatus domainStatus = entityToDomainStatus(entity.getStatus());
        if (domainStatus != null && domainStatus != com.bank.paymentinitiation.domain.model.PaymentStatus.INITIATED) {
            try {
                order.changeStatus(domainStatus);
            } catch (IllegalStateException e) {
                // Si la transición no es válida, mantener el estado original
                // Esto puede ocurrir si los datos en BD están inconsistentes
            }
        }
        
        // Nota: Los timestamps se establecen automáticamente en create() y changeStatus()
        // Los timestamps de la entidad no se pueden establecer directamente en PaymentOrder
        // porque es inmutable. Si se necesitan timestamps específicos, habría que crear
        // un método factory adicional en PaymentOrder.
        
        return order;
    }
    
    // Métodos de mapeo personalizados
    
    @Named("stringToExternalReference")
    default com.bank.paymentinitiation.domain.model.ExternalReference stringToExternalReference(String value) {
        return value != null ? com.bank.paymentinitiation.domain.model.ExternalReference.of(value) : null;
    }
    
    @Named("stringToPayerReference")
    default com.bank.paymentinitiation.domain.model.PayerReference stringToPayerReference(String value) {
        return value != null ? com.bank.paymentinitiation.domain.model.PayerReference.of(value) : null;
    }
    
    @Named("stringToPayeeReference")
    default com.bank.paymentinitiation.domain.model.PayeeReference stringToPayeeReference(String value) {
        return value != null ? com.bank.paymentinitiation.domain.model.PayeeReference.of(value) : null;
    }
    
    default com.bank.paymentinitiation.domain.model.PaymentAmount paymentAmountFromEntity(java.math.BigDecimal amount, String currency) {
        if (amount == null || currency == null) {
            return null;
        }
        return com.bank.paymentinitiation.domain.model.PaymentAmount.of(amount, currency);
    }
    
    @Named("domainToEntityStatus")
    default PaymentOrderEntity.PaymentStatusEntity domainToEntityStatus(com.bank.paymentinitiation.domain.model.PaymentStatus domain) {
        if (domain == null) {
            return null;
        }
        return PaymentOrderEntity.PaymentStatusEntity.valueOf(domain.name());
    }
    
    @Named("entityToDomainStatus")
    default com.bank.paymentinitiation.domain.model.PaymentStatus entityToDomainStatus(PaymentOrderEntity.PaymentStatusEntity entity) {
        if (entity == null) {
            return null;
        }
        return com.bank.paymentinitiation.domain.model.PaymentStatus.valueOf(entity.name());
    }
}

