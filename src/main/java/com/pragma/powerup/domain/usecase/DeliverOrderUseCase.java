package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDeliverOrderServicePort;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.time.LocalDateTime;

public class DeliverOrderUseCase implements IDeliverOrderServicePort {

  private final IOrderPersistencePort orderPersistencePort;
  private final ITraceabilityServicePort traceabilityServicePort;
  private final IUserServicePort userServicePort;

  public DeliverOrderUseCase(
      IOrderPersistencePort orderPersistencePort,
      ITraceabilityServicePort traceabilityServicePort,
      IUserServicePort userServicePort) {
    this.orderPersistencePort = orderPersistencePort;
    this.traceabilityServicePort = traceabilityServicePort;
    this.userServicePort = userServicePort;
  }

  @Override
  public Order deliver(Long orderId, String pin, Long employeeId) {
    Order order =
        orderPersistencePort
            .findById(orderId)
            .orElseThrow(() -> new DomainException("Order not found"));
    if (order.getStatus() != OrderStatus.LISTO) {
      throw new DomainException("Only LISTO orders can be delivered");
    }
    if (order.getPin() == null || !order.getPin().equals(pin)) {
      throw new DomainException("Invalid PIN");
    }

    // Validate employee belongs to the restaurant that owns the order
    if (!userServicePort.isEmployeeOfRestaurant(employeeId, order.getRestaurantId())) {
      throw new DomainException(
          "Employee is not authorized to deliver orders from this restaurant");
    }

    // Get employee name for traceability
    String employeeName = userServicePort.getUserName(employeeId);

    String previousStatus = order.getStatus().name();
    order.setStatus(OrderStatus.ENTREGADO);
    Order savedOrder = orderPersistencePort.save(order);

    // Register traceability event using internal service
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(savedOrder.getId())
            .customerId(savedOrder.getCustomerId())
            .restaurantId(savedOrder.getRestaurantId())
            .eventType("ORDER_STATUS_CHANGE")
            .previousStatus(previousStatus)
            .newStatus(savedOrder.getStatus().name())
            .timestamp(LocalDateTime.now())
            .employeeId(employeeId)
            .employeeName(employeeName)
            .build();
    traceabilityServicePort.createTraceabilityEvent(event);

    return savedOrder;
  }
}
