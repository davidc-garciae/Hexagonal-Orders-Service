package com.pragma.powerup.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTraceabilityIntegrationServiceTest {

  @Mock private ITraceabilityServicePort traceabilityServicePort;

  @InjectMocks private OrderTraceabilityIntegrationService integrationService;

  @Test
  void createOrderStatusChangeEvent_ShouldCreateCorrectTraceabilityEvent() {
    // Given
    Long orderId = 1L;
    Long customerId = 2L;
    Long restaurantId = 3L;
    String previousStatus = "PENDING";
    String newStatus = "IN_PREPARATION";
    Long employeeId = 4L;

    ArgumentCaptor<TraceabilityEvent> eventCaptor =
        ArgumentCaptor.forClass(TraceabilityEvent.class);

    // When
    integrationService.createOrderStatusChangeEvent(
        orderId, customerId, restaurantId, previousStatus, newStatus, employeeId);

    // Then
    verify(traceabilityServicePort, times(1)).createTraceabilityEvent(eventCaptor.capture());

    TraceabilityEvent capturedEvent = eventCaptor.getValue();
    assertEquals(orderId, capturedEvent.getOrderId());
    assertEquals(customerId, capturedEvent.getCustomerId());
    assertEquals(restaurantId, capturedEvent.getRestaurantId());
    assertEquals("ORDER_STATUS_CHANGE", capturedEvent.getEventType());
    assertEquals(previousStatus, capturedEvent.getPreviousStatus());
    assertEquals(newStatus, capturedEvent.getNewStatus());
    assertEquals(employeeId, capturedEvent.getEmployeeId());
    assertNotNull(capturedEvent.getTimestamp());
    assertTrue(capturedEvent.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  void createOrderAssignmentEvent_ShouldCreateCorrectEvent() {
    // Given
    Long orderId = 1L;
    Long customerId = 2L;
    Long restaurantId = 3L;
    Long employeeId = 4L;

    ArgumentCaptor<TraceabilityEvent> eventCaptor =
        ArgumentCaptor.forClass(TraceabilityEvent.class);

    // When
    integrationService.createOrderAssignmentEvent(orderId, customerId, restaurantId, employeeId);

    // Then
    verify(traceabilityServicePort, times(1)).createTraceabilityEvent(eventCaptor.capture());

    TraceabilityEvent capturedEvent = eventCaptor.getValue();
    assertEquals("PENDING", capturedEvent.getPreviousStatus());
    assertEquals("IN_PREPARATION", capturedEvent.getNewStatus());
    assertEquals(employeeId, capturedEvent.getEmployeeId());
  }

  @Test
  void createOrderReadyEvent_ShouldCreateCorrectEvent() {
    // Given
    Long orderId = 1L;
    Long customerId = 2L;
    Long restaurantId = 3L;
    Long employeeId = 4L;

    ArgumentCaptor<TraceabilityEvent> eventCaptor =
        ArgumentCaptor.forClass(TraceabilityEvent.class);

    // When
    integrationService.createOrderReadyEvent(orderId, customerId, restaurantId, employeeId);

    // Then
    verify(traceabilityServicePort, times(1)).createTraceabilityEvent(eventCaptor.capture());

    TraceabilityEvent capturedEvent = eventCaptor.getValue();
    assertEquals("IN_PREPARATION", capturedEvent.getPreviousStatus());
    assertEquals("READY", capturedEvent.getNewStatus());
    assertEquals(employeeId, capturedEvent.getEmployeeId());
  }

  @Test
  void createOrderDeliveredEvent_ShouldCreateCorrectEvent() {
    // Given
    Long orderId = 1L;
    Long customerId = 2L;
    Long restaurantId = 3L;
    Long employeeId = 4L;

    ArgumentCaptor<TraceabilityEvent> eventCaptor =
        ArgumentCaptor.forClass(TraceabilityEvent.class);

    // When
    integrationService.createOrderDeliveredEvent(orderId, customerId, restaurantId, employeeId);

    // Then
    verify(traceabilityServicePort, times(1)).createTraceabilityEvent(eventCaptor.capture());

    TraceabilityEvent capturedEvent = eventCaptor.getValue();
    assertEquals("READY", capturedEvent.getPreviousStatus());
    assertEquals("DELIVERED", capturedEvent.getNewStatus());
    assertEquals(employeeId, capturedEvent.getEmployeeId());
  }

  @Test
  void createOrderCancelledEvent_ShouldCreateCorrectEvent() {
    // Given
    Long orderId = 1L;
    Long customerId = 2L;
    Long restaurantId = 3L;
    String reason = "Customer requested cancellation";

    ArgumentCaptor<TraceabilityEvent> eventCaptor =
        ArgumentCaptor.forClass(TraceabilityEvent.class);

    // When
    integrationService.createOrderCancelledEvent(orderId, customerId, restaurantId, reason);

    // Then
    verify(traceabilityServicePort, times(1)).createTraceabilityEvent(eventCaptor.capture());

    TraceabilityEvent capturedEvent = eventCaptor.getValue();
    assertEquals("ORDER_CANCELLED", capturedEvent.getEventType());
    assertEquals("PENDING", capturedEvent.getPreviousStatus());
    assertEquals("CANCELLED", capturedEvent.getNewStatus());
    assertNull(capturedEvent.getEmployeeId()); // No employee for cancellations
  }
}
