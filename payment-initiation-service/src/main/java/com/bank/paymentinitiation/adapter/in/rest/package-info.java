/**
 * Adaptadores de entrada REST.
 * 
 * <p>Este paquete contiene los adaptadores que exponen el dominio a través de HTTP REST:
 * <ul>
 *   <li>PaymentOrdersController: Implementa PaymentOrdersApi (generado por OpenAPI)</li>
 *   <li>GlobalExceptionHandler: Maneja excepciones y las convierte a ProblemDetail (RFC 7807)</li>
 * </ul>
 * 
 * <p>Los adaptadores REST:
 * <ul>
 *   <li>Reciben requests HTTP y los mapean a objetos del dominio</li>
 *   <li>Invocan los casos de uso (servicios de aplicación)</li>
 *   <li>Mapean las respuestas del dominio a DTOs REST</li>
 * </ul>
 */
package com.bank.paymentinitiation.adapter.in.rest;

