package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateTraceabilityEventUseCaseTest {

  private final ITraceabilityPersistencePort traceabilityPersistencePort =
      mock(ITraceabilityPersistencePort.class);
  private final IUserServicePort userServicePort = mock(IUserServicePort.class);

  private ITraceabilityServicePort useCase;

  @BeforeEach
  void setUp() {
    useCase = new TraceabilityUseCase(traceabilityPersistencePort, userServicePort);
  }

  @Test
  @DisplayName("Should create traceability event when data is valid")
  void shouldCreateTraceabilityEventWhenValid() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(789L)
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    // Act
    useCase.createTraceabilityEvent(event);

    // Assert
    verify(traceabilityPersistencePort).saveEvent(event);
  }

  @Test
  @DisplayName("Should create status change event when data is valid")
  void shouldCreateStatusChangeEventWhenValid() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(789L)
            .restaurantId(456L)
            .eventType("STATUS_CHANGED")
            .previousStatus("PENDIENTE")
            .newStatus("EN_PREPARACION")
            .timestamp(LocalDateTime.now())
            .employeeId(321L)
            .build();

    // Act
    useCase.createTraceabilityEvent(event);

    // Assert
    verify(traceabilityPersistencePort).saveEvent(event);
  }

  @Test
  @DisplayName("Should throw exception when event is null")
  void shouldThrowExceptionWhenEventIsNull() {
    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event cannot be null");
  }

  @Test
  @DisplayName("Should throw exception when order ID is null")
  void shouldThrowExceptionWhenOrderIdIsNull() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(null) // Missing
            .customerId(789L)
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(event))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order ID cannot be null");
  }

  @Test
  @DisplayName("Should throw exception when customer ID is null")
  void shouldThrowExceptionWhenCustomerIdIsNull() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(null) // Missing
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(event))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Customer ID cannot be null");
  }

  @Test
  @DisplayName("Should throw exception when restaurant ID is null")
  void shouldThrowExceptionWhenRestaurantIdIsNull() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(789L)
            .restaurantId(null) // Missing
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(event))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Restaurant ID cannot be null");
  }

  @Test
  @DisplayName("Should throw exception when event type is null")
  void shouldThrowExceptionWhenEventTypeIsNull() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(789L)
            .restaurantId(456L)
            .eventType(null) // Missing
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(event))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event type cannot be null");
  }

  @Test
  @DisplayName("Should throw exception when new status is null")
  void shouldThrowExceptionWhenNewStatusIsNull() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(789L)
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus(null) // Missing
            .timestamp(LocalDateTime.now())
            .build();

    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(event))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("New status cannot be null");
  }

  @Test
  @DisplayName("Should throw exception when timestamp is null")
  void shouldThrowExceptionWhenTimestampIsNull() {
    // Arrange
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(123L)
            .customerId(789L)
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(null) // Missing
            .build();

    // Act & Assert
    assertThatThrownBy(() -> useCase.createTraceabilityEvent(event))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Timestamp cannot be null");
  }
}
