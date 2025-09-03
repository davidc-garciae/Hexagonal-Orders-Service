package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICancelOrderServicePort;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IUserFeignPort;
import java.time.LocalDateTime;

public class CancelOrderUseCase implements ICancelOrderServicePort {

  private final IOrderPersistencePort orderPersistencePort;
  private final IUserFeignPort userFeignPort;
  private final ITraceabilityServicePort traceabilityServicePort;

  public CancelOrderUseCase(
      IOrderPersistencePort orderPersistencePort,
      IUserFeignPort userFeignPort,
      ITraceabilityServicePort traceabilityServicePort) {
    this.orderPersistencePort = orderPersistencePort;
    this.userFeignPort = userFeignPort;
    this.traceabilityServicePort = traceabilityServicePort;
  }

  @Override
  public Order cancel(Long orderId, Long customerId) {
    Order o =
        orderPersistencePort
            .findById(orderId)
            .orElseThrow(() -> new DomainException("Order not found"));

    // Validate customer exists
    userFeignPort.getUserById(customerId);

    if (!customerId.equals(o.getCustomerId())) {
      throw new DomainException("Forbidden");
    }
    if (o.getStatus() != OrderStatus.PENDIENTE) {
      throw new DomainException(
          "Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
    }

    String previousStatus = o.getStatus().name();
    o.setStatus(OrderStatus.CANCELADO);
    Order savedOrder = orderPersistencePort.save(o);

    // Register traceability event using internal service
    TraceabilityEvent event =
        TraceabilityEvent.builder()
            .orderId(savedOrder.getId())
            .eventType("ORDER_STATUS_CHANGE")
            .previousStatus(previousStatus)
            .newStatus(savedOrder.getStatus().name())
            .timestamp(LocalDateTime.now())
            .customerId(customerId)
            .restaurantId(savedOrder.getRestaurantId())
            .build();
    traceabilityServicePort.createTraceabilityEvent(event);

    return savedOrder;
  }
}
