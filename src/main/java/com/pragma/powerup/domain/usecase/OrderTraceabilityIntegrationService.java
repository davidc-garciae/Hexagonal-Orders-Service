package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to integrate traceability event creation with order status changes This demonstrates how
 * existing order operations can be extended to create traceability events
 */
@Service
@RequiredArgsConstructor
public class OrderTraceabilityIntegrationService {

  private final ITraceabilityServicePort traceabilityServicePort;

  /**
   * Create a traceability event when an order status changes This method should be called from
   * existing use cases like MarkOrderReadyUseCase, AssignOrderUseCase, etc.
   *
   * @param orderId The order ID
   * @param customerId The customer ID
   * @param restaurantId The restaurant ID
   * @param previousStatus The previous order status
   * @param newStatus The new order status
   * @param employeeId The employee who made the change (can be null)
   */
  public void createOrderStatusChangeEvent(
      Long orderId,
      Long customerId,
      Long restaurantId,
      String previousStatus,
      String newStatus,
      Long employeeId) {
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(customerId)
            .restaurantId(restaurantId)
            .eventType("ORDER_STATUS_CHANGE")
            .previousStatus(previousStatus)
            .newStatus(newStatus)
            .timestamp(LocalDateTime.now())
            .employeeId(employeeId)
            .build();

    traceabilityServicePort.createTraceabilityEvent(event);
  }

  /**
   * Create a traceability event when an order is assigned to an employee
   *
   * @param orderId The order ID
   * @param customerId The customer ID
   * @param restaurantId The restaurant ID
   * @param employeeId The employee ID assigned to the order
   */
  public void createOrderAssignmentEvent(
      Long orderId, Long customerId, Long restaurantId, Long employeeId) {
    createOrderStatusChangeEvent(
        orderId, customerId, restaurantId, "PENDING", "IN_PREPARATION", employeeId);
  }

  /**
   * Create a traceability event when an order is marked as ready
   *
   * @param orderId The order ID
   * @param customerId The customer ID
   * @param restaurantId The restaurant ID
   * @param employeeId The employee who prepared the order
   */
  public void createOrderReadyEvent(
      Long orderId, Long customerId, Long restaurantId, Long employeeId) {
    createOrderStatusChangeEvent(
        orderId, customerId, restaurantId, "IN_PREPARATION", "READY", employeeId);
  }

  /**
   * Create a traceability event when an order is delivered
   *
   * @param orderId The order ID
   * @param customerId The customer ID
   * @param restaurantId The restaurant ID
   * @param employeeId The employee who delivered the order
   */
  public void createOrderDeliveredEvent(
      Long orderId, Long customerId, Long restaurantId, Long employeeId) {
    createOrderStatusChangeEvent(
        orderId, customerId, restaurantId, "READY", "DELIVERED", employeeId);
  }

  /**
   * Create a traceability event when an order is cancelled
   *
   * @param orderId The order ID
   * @param customerId The customer ID
   * @param restaurantId The restaurant ID
   * @param reason The cancellation reason
   */
  public void createOrderCancelledEvent(
      Long orderId, Long customerId, Long restaurantId, String reason) {
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(orderId)
            .customerId(customerId)
            .restaurantId(restaurantId)
            .eventType("ORDER_CANCELLED")
            .previousStatus("PENDING") // Could be any status
            .newStatus("CANCELLED")
            .timestamp(LocalDateTime.now())
            .build();

    traceabilityServicePort.createTraceabilityEvent(event);
  }
}
