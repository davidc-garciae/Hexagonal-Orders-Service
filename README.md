# orders-service (Spring Boot, Hexagonal)

-   Java 17 · Spring Boot 3 · Gradle 8
-   Hexagonal Architecture (domain, application, infrastructure)
-   OpenAPI 3.0 (springdoc)
-   Seguridad: Gateway valida JWT; este servicio autoriza por rol con headers `X-User-*`
-   Testing: JUnit 5, WebMvc slice, ArchUnit, JaCoCo (≥80%)

## Funcionalidad cubierta (Historias de Usuario)

-   HU-011: Crear Pedido (CUSTOMER)
-   HU-012: Listar pedidos por estado (OWNER/EMPLOYEE)
-   HU-013: Asignarse a pedido (EMPLOYEE) → PENDIENTE → EN_PREPARACION
-   HU-014: Notificar pedido listo (EMPLOYEE) → EN_PREPARACION → LISTO + PIN + evento
-   HU-015: Entregar pedido (EMPLOYEE) → valida PIN → ENTREGADO
-   HU-016: Cancelar pedido (CUSTOMER) → solo PENDIENTE → CANCELADO

## Endpoints principales

-   POST `/api/v1/orders` → Crear pedido (CUSTOMER)
-   GET `/api/v1/orders?status=&restaurantId=&page=&size=` → Listado por estado (OWNER/EMPLOYEE)
-   PUT `/api/v1/orders/{id}/assign` → Asignar empleado (EMPLOYEE)
-   PUT `/api/v1/orders/{id}/ready` → Marcar como LISTO (genera PIN y publica evento)
-   PUT `/api/v1/orders/{id}/deliver` → Entregar (valida PIN) (EMPLOYEE)
-   PUT `/api/v1/orders/{id}/cancel` → Cancelar (CUSTOMER, solo PENDIENTE)

Documentación OpenAPI: ver `docs/openapi/orders.yaml`.

## Seguridad

-   El API Gateway valida JWT y propaga `X-User-Id`, `X-User-Email`, `X-User-Role`.
-   Este servicio usa `HeaderAuthenticationFilter` y `@PreAuthorize`:
    -   `CUSTOMER` para crear/cancelar
    -   `EMPLOYEE` para asignar, marcar listo y entregar
    -   `EMPLOYEE`/`OWNER` para listado por estado

## Ejecución local

```bash
./gradlew spotlessApply
./gradlew test jacocoTestReport
./gradlew bootRun
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

Variables relevantes (`application.yml`/env):

-   `OTEL_EXPORTER_OTLP_ENDPOINT` (tracing)
-   `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

## Arquitectura (resumen)

-   Domain:
    -   Modelos: `Order`, `OrderItem`, `OrderStatus`
    -   Casos de uso: `CreateOrderUseCase`, `ListOrdersByStatusUseCase`, `AssignOrderUseCase`, `MarkOrderReadyUseCase`, `DeliverOrderUseCase`, `CancelOrderUseCase`
    -   Ports IN: `IOrderServicePort`
    -   Ports OUT: `IOrderPersistencePort`, `IOrderEventPublisherPort`
-   Application:
    -   Handlers: `OrderHandler`, `OrderQueryHandler`, `OrderAssignHandler`, `OrderReadyHandler`, `OrderDeliverHandler`, `OrderCancelHandler`
    -   DTOs y Mappers: MapStruct (request/response)
-   Infrastructure:
    -   REST: `OrderRestController`
    -   JPA: `OrderJpaAdapter`, `OrderRepository`, `OrderEntity`, `OrderItemEntity`, `IOrderEntityMapper`
    -   Messaging (placeholder): `NoOpOrderEventPublisherAdapter` (listo para conectar con mensajería externa)
    -   Seguridad: `SecurityConfiguration`, `HeaderAuthenticationFilter`, `RoleConstants`
    -   Configuración beans: `BeanConfiguration`

## Tests

-   Dominio: casos de uso (creación, listado, asignación, listo, entrega con PIN, cancelación)
-   WebMvc: `OrderRestControllerWebMvcTest`
-   Arquitectura: `HexagonalArchitectureTest`

## Integraciones con otros microservicios

-   Asíncrono (publicación de eventos)

    -   HU-014 publica evento de cambio de estado (LISTO) con PIN (adaptador actual es NoOp; se integrará con messaging-service en la siguiente iteración)

-   Dependencias síncronas (salientes)
    -   No aplica en esta versión (validaciones de pertenencia de empleado/propietario se dejarán a servicios de usuarios en iteraciones posteriores).

## Seguridad y Gateway

-   Gateway: valida JWT y propaga `X-User-*`.
-   Servicio: `@PreAuthorize` por rol y validaciones con `X-User-Id` en controlador.

## Diagrama y documentación

-   HU Diagrams: `docs/diagrams/HU/` (HU-011..HU-016)
-   Requisitos: `docs/Requirements.md`
-   Guía ampliada: `docs/README.microservicios.md`

## Convenciones y CI

-   Formato: Spotless (Google Java Format)
-   Cobertura: JaCoCo ≥ 80% (`./gradlew check`)
-   Ramas por HU: `feature/HU-xxx-descripcion`; tag al merge de cada HU
