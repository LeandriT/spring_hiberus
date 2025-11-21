package com.bank.paymentinitiation.adapter.out.persistence.mapper;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

/**
 * Mapper between Domain Objects and Persistence Entities (JPA).
 * 
 * <p>This mapper handles conversion between:
 * <ul>
 *   <li>Domain Objects (PaymentOrder, PaymentStatus, PaymentAmount, etc.)</li>
 *   <li>Persistence Entities (PaymentOrderEntity, etc.)</li>
 * </ul>
 * 
 * <p>Mappings:
 * <ul>
 *   <li>Domain value objects (ExternalReference, PayerReference, PayeeReference) → String</li>
 *   <li>Domain PaymentAmount → BigDecimal amount + String currency</li>
 *   <li>Domain PaymentStatus enum → String (enum name)</li>
 *   <li>Domain paymentOrderReference → Entity id (UUID) and paymentOrderReference (String)</li>
 * </ul>
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentOrderPersistenceMapper {

    /**
     * Maps PaymentOrder (Domain) to PaymentOrderEntity (Persistence).
     * 
     * <p>Converts:
     * <ul>
     *   <li>Value objects to strings (ExternalReference, PayerReference, PayeeReference)</li>
     *   <li>PaymentAmount to amount (BigDecimal) and currency (String)</li>
     *   <li>PaymentStatus enum to String (enum name)</li>
     *   <li>Generates UUID for id if entity is new</li>
     * </ul>
     * 
     * @param domain the domain PaymentOrder
     * @return the persistence PaymentOrderEntity
     */
    @Mapping(target = "id", expression = "java(generateIdIfNew(domain))")
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(domain.getExternalReference().getValue())")
    @Mapping(target = "payerReference", expression = "java(domain.getPayerReference().getValue())")
    @Mapping(target = "payeeReference", expression = "java(domain.getPayeeReference().getValue())")
    @Mapping(target = "amount", expression = "java(domain.getInstructedAmount().getValue())")
    @Mapping(target = "currency", expression = "java(domain.getInstructedAmount().getCurrency())")
    @Mapping(target = "remittanceInformation", source = "remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate")
    @Mapping(target = "status", expression = "java(domain.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PaymentOrderEntity toEntity(PaymentOrder domain);

    /**
     * Maps PaymentOrderEntity (Persistence) to PaymentOrder (Domain).
     * 
     * <p>Converts:
     * <ul>
     *   <li>Strings to value objects (ExternalReference, PayerReference, PayeeReference)</li>
     *   <li>amount (BigDecimal) + currency (String) to PaymentAmount</li>
     *   <li>String status to PaymentStatus enum</li>
     *   <li>Entity paymentOrderReference to domain paymentOrderReference</li>
     * </ul>
     * 
     * @param entity the persistence PaymentOrderEntity
     * @return the domain PaymentOrder
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(new com.bank.paymentinitiation.domain.model.ExternalReference(entity.getExternalReference()))")
    @Mapping(target = "payerReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayerReference(entity.getPayerReference()))")
    @Mapping(target = "payeeReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayeeReference(entity.getPayeeReference()))")
    @Mapping(target = "instructedAmount", expression = "java(com.bank.paymentinitiation.domain.model.PaymentAmount.of(entity.getAmount(), entity.getCurrency()))")
    @Mapping(target = "remittanceInformation", source = "remittanceInformation")
    @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate")
    @Mapping(target = "status", expression = "java(com.bank.paymentinitiation.domain.model.PaymentStatus.valueOf(entity.getStatus()))")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PaymentOrder toDomain(PaymentOrderEntity entity);


    /**
     * Helper method to generate a UUID for new entities.
     * 
     * <p>This method is used in the mapping expression to generate an ID
     * when creating a new entity. For existing entities, the ID should
     * be set from the database.
     * 
     * @param domain the domain PaymentOrder
     * @return a new UUID for new entities
     */
    default UUID generateIdIfNew(PaymentOrder domain) {
        // For new entities, generate a UUID
        // For existing entities, the ID will be set when loading from DB
        return UUID.randomUUID();
    }
}
