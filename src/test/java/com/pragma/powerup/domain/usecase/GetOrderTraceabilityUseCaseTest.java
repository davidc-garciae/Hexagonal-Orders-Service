package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetOrderTraceabilityUseCaseTest {

  private final ITraceabilityPersistencePort traceabilityPersistencePort =
      mock(ITraceabilityPersistencePort.class);
  private final IUserServicePort userServicePort = mock(IUserServicePort.class);

  private ITraceabilityServicePort useCase;

  @BeforeEach
  void setUp() {
    useCase = new TraceabilityUseCase(traceabilityPersistencePort, userServicePort);
  }

  @Test
  @DisplayName("Should return traceability events when customer requests their own order")
  void shouldReturnTraceabilityWhenCustomerOwnsOrder() {
    // Arrange
    Long orderId = 123L;
    Long customerId = 789L;
    String userRole = "CUSTOMER";

    TraceabilityEvent event1 =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(customerId)
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now().minusHours(2))
            .build();

    TraceabilityEvent event2 =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(customerId)
            .restaurantId(456L)
            .eventType("STATUS_CHANGED")
            .previousStatus("PENDIENTE")
            .newStatus("EN_PREPARACION")
            .timestamp(LocalDateTime.now().minusHours(1))
            .employeeId(321L)
            .build();

    List<TraceabilityEvent> expectedEvents = Arrays.asList(event1, event2);

    when(traceabilityPersistencePort.findEventsByOrderId(orderId)).thenReturn(expectedEvents);

    // Act
    List<TraceabilityEvent> result = useCase.getOrderTraceability(orderId, customerId, userRole);

    // Assert
    assertThat(result).hasSize(2).containsExactlyElementsOf(expectedEvents);
    verify(traceabilityPersistencePort).findEventsByOrderId(orderId);
  }

  @Test
  @DisplayName("Should throw exception when customer requests other customer's order")
  void shouldThrowExceptionWhenCustomerRequestsOtherOrder() {
    // Arrange
    Long orderId = 123L;
    Long requestingCustomerId = 789L;
    Long orderOwnerCustomerId = 999L;
    String userRole = "CUSTOMER";

    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(orderOwnerCustomerId) // Different customer
            .restaurantId(456L)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    when(traceabilityPersistencePort.findEventsByOrderId(orderId))
        .thenReturn(Collections.singletonList(event));

    // Act & Assert
    assertThatThrownBy(() -> useCase.getOrderTraceability(orderId, requestingCustomerId, userRole))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("You are not authorized to view this order's traceability");
  }

  @Test
  @DisplayName("Should throw exception when order not found")
  void shouldThrowExceptionWhenOrderNotFound() {
    // Arrange
    Long orderId = 123L;
    Long customerId = 789L;
    String userRole = "CUSTOMER";

    when(traceabilityPersistencePort.findEventsByOrderId(orderId))
        .thenReturn(Collections.emptyList());

    // Act & Assert
    assertThatThrownBy(() -> useCase.getOrderTraceability(orderId, customerId, userRole))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Order not found: " + orderId);
  }

  @Test
  @DisplayName("Should allow owner to view orders from their restaurant")
  void shouldAllowOwnerToViewRestaurantOrders() {
    // Arrange
    Long orderId = 123L;
    Long ownerId = 100L;
    Long restaurantId = 456L;
    String userRole = "OWNER";

    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(789L)
            .restaurantId(restaurantId)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    when(traceabilityPersistencePort.findEventsByOrderId(orderId))
        .thenReturn(Collections.singletonList(event));
    when(userServicePort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);

    // Act
    List<TraceabilityEvent> result = useCase.getOrderTraceability(orderId, ownerId, userRole);

    // Assert
    assertThat(result).hasSize(1).containsExactly(event);
    verify(userServicePort).isOwnerOfRestaurant(ownerId, restaurantId);
  }

  @Test
  @DisplayName("Should throw exception when owner requests order from other restaurant")
  void shouldThrowExceptionWhenOwnerRequestsOtherRestaurantOrder() {
    // Arrange
    Long orderId = 123L;
    Long ownerId = 100L;
    Long restaurantId = 456L;
    String userRole = "OWNER";

    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(789L)
            .restaurantId(restaurantId)
            .eventType("ORDER_CREATED")
            .newStatus("PENDIENTE")
            .timestamp(LocalDateTime.now())
            .build();

    when(traceabilityPersistencePort.findEventsByOrderId(orderId))
        .thenReturn(Collections.singletonList(event));
    when(userServicePort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(false);

    // Act & Assert
    assertThatThrownBy(() -> useCase.getOrderTraceability(orderId, ownerId, userRole))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("You are not authorized to view this order's traceability");
  }

  @Test
  @DisplayName("Should throw exception when parameters are null")
  void shouldThrowExceptionWhenParametersAreNull() {
    // Act & Assert
    assertThatThrownBy(() -> useCase.getOrderTraceability(null, 123L, "CUSTOMER"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order ID cannot be null");

    assertThatThrownBy(() -> useCase.getOrderTraceability(123L, null, "CUSTOMER"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Requesting user ID cannot be null");

    assertThatThrownBy(() -> useCase.getOrderTraceability(123L, 789L, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User role cannot be null or empty");

    assertThatThrownBy(() -> useCase.getOrderTraceability(123L, 789L, ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User role cannot be null or empty");
  }
}
