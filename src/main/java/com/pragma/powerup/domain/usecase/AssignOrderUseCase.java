package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAssignOrderServicePort;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IUserFeignPort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.time.LocalDateTime;

public class AssignOrderUseCase implements IAssignOrderServicePort {

  private final IOrderPersistencePort orderPersistencePort;
  private final IUserFeignPort userFeignPort;
  private final ITraceabilityServicePort traceabilityServicePort;
  private final IUserServicePort userServicePort;

  public AssignOrderUseCase(
      IOrderPersistencePort orderPersistencePort,
      IUserFeignPort userFeignPort,
      ITraceabilityServicePort traceabilityServicePort,
      IUserServicePort userServicePort) {
    this.orderPersistencePort = orderPersistencePort;
    this.userFeignPort = userFeignPort;
    this.traceabilityServicePort = traceabilityServicePort;
    this.userServicePort = userServicePort;
  }

  @Override
  public Order assignOrder(Long orderId, Long employeeId) {
    Order order =
        orderPersistencePort
            .findById(orderId)
            .orElseThrow(() -> new DomainException("Order not found"));
    if (order.getStatus() != OrderStatus.PENDIENTE) {
      throw new DomainException("Only PENDIENTE orders can be assigned");
    }

    // Validate employee exists and has proper role
    userFeignPort.getUserById(employeeId);

    // Validate employee belongs to the restaurant that owns the order
    if (!userServicePort.isEmployeeOfRestaurant(employeeId, order.getRestaurantId())) {
      throw new DomainException("Employee is not authorized to assign orders from this restaurant");
    }

    // Get employee name for traceability
    String employeeName = userServicePort.getUserName(employeeId);

    String previousStatus = order.getStatus().name();
    order.setEmployeeId(employeeId);
    order.setStatus(OrderStatus.EN_PREPARACION);
    Order saved = orderPersistencePort.save(order);

    // Create traceability event
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(saved.getId())
            .customerId(saved.getCustomerId())
            .restaurantId(saved.getRestaurantId())
            .eventType("ORDER_STATUS_CHANGE")
            .previousStatus(previousStatus)
            .newStatus(saved.getStatus().name())
            .timestamp(LocalDateTime.now())
            .employeeId(employeeId)
            .employeeName(employeeName)
            .build();
    traceabilityServicePort.createTraceabilityEvent(event);

    return saved;
  }
}
