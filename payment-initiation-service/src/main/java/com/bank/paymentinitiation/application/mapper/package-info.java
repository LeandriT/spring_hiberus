/**
 * Application Mappers Package (Optional).
 * 
 * <p>Contains mappers used by application services to convert between:
 * <ul>
 *   <li>REST DTOs (from adapter.in.rest) and Domain Objects</li>
 *   <li>Domain Objects and Persistence Entities (in coordination with adapter.out.persistence.mapper)</li>
 * </ul>
 * 
 * <p>These mappers can be implemented using MapStruct for type-safe, efficient mapping.
 * 
 * <p>Note: If mapping logic is simple, it can be done directly in application services.
 * This package is optional and can be used if mapping becomes complex or needs to be reused.
 */
package com.bank.paymentinitiation.application.mapper;

