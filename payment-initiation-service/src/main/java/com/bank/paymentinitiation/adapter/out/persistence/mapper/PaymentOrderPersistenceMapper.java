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

/**
 * Mapper MapStruct para convertir entre PaymentOrder (dominio) y PaymentOrderEntity (JPA).
 * 
 * <p>Este mapper maneja la conversión entre:
 * <ul>
 *   <li>Value objects del dominio ↔ campos primitivos de la entidad</li>
 *   <li>PaymentStatus enum ↔ String</li>
 *   <li>paymentOrderReference ↔ id (UUID técnico vs referencia de negocio)</li>
 * </ul>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentOrderPersistenceMapper {

    /**
     * Convierte un PaymentOrder del dominio a PaymentOrderEntity (JPA).
     * 
     * <p>Mapea value objects a campos primitivos:
     * <ul>
     *   <li>ExternalReference.getValue() → externalReference</li>
     *   <li>PayerReference.getValue() → payerReference</li>
     *   <li>PayeeReference.getValue() → payeeReference</li>
     *   <li>PaymentAmount.getValue() → amount</li>
     *   <li>PaymentAmount.getCurrency() → currency</li>
     *   <li>PaymentStatus.name() → status</li>
     * </ul>
     *
     * @param domain el PaymentOrder del dominio
     * @return la PaymentOrderEntity para persistir
     */
    @Mapping(target = "id", ignore = true) // El ID se maneja en el adaptador
    @Mapping(target = "externalReference", expression = "java(domain.getExternalReference().getValue())")
    @Mapping(target = "payerReference", expression = "java(domain.getPayerReference().getValue())")
    @Mapping(target = "payeeReference", expression = "java(domain.getPayeeReference().getValue())")
    @Mapping(target = "amount", expression = "java(domain.getInstructedAmount().getValue())")
    @Mapping(target = "currency", expression = "java(domain.getInstructedAmount().getCurrency())")
    @Mapping(target = "status", expression = "java(domain.getStatus().name())")
    PaymentOrderEntity toEntity(PaymentOrder domain);

    /**
     * Convierte un PaymentOrderEntity (JPA) a PaymentOrder del dominio.
     * 
     * <p>Reconstruye value objects desde campos primitivos:
     * <ul>
     *   <li>externalReference → new ExternalReference(...)</li>
     *   <li>payerReference → new PayerReference(...)</li>
     *   <li>payeeReference → new PayeeReference(...)</li>
     *   <li>amount + currency → PaymentAmount.of(...)</li>
     *   <li>status → PaymentStatus.valueOf(...)</li>
     * </ul>
     *
     * @param entity la PaymentOrderEntity de la base de datos
     * @return el PaymentOrder del dominio
     */
    @Mapping(target = "paymentOrderReference", source = "paymentOrderReference")
    @Mapping(target = "externalReference", expression = "java(new com.bank.paymentinitiation.domain.model.ExternalReference(entity.getExternalReference()))")
    @Mapping(target = "payerReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayerReference(entity.getPayerReference()))")
    @Mapping(target = "payeeReference", expression = "java(new com.bank.paymentinitiation.domain.model.PayeeReference(entity.getPayeeReference()))")
    @Mapping(target = "instructedAmount", expression = "java(com.bank.paymentinitiation.domain.model.PaymentAmount.of(entity.getAmount(), entity.getCurrency()))")
    @Mapping(target = "status", expression = "java(com.bank.paymentinitiation.domain.model.PaymentStatus.valueOf(entity.getStatus()))")
    PaymentOrder toDomain(PaymentOrderEntity entity);
}

