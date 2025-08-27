package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderEventPublisherPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.security.SecureRandom;

public class MarkOrderReadyUseCase implements IOrderServicePort {

  private final IOrderPersistencePort orderPersistencePort;
  private final IOrderEventPublisherPort eventPublisherPort;
  private final SecureRandom random = new SecureRandom();

  public MarkOrderReadyUseCase(
      IOrderPersistencePort orderPersistencePort, IOrderEventPublisherPort eventPublisherPort) {
    this.orderPersistencePort = orderPersistencePort;
    this.eventPublisherPort = eventPublisherPort;
  }

  @Override
  public Order createOrder(Order order) {
    throw new UnsupportedOperationException("Not supported in this use case");
  }

  @Override
  public PagedResult<Order> listByStatusAndRestaurant(
      Long restaurantId, OrderStatus status, int page, int size) {
    throw new UnsupportedOperationException("Not supported in this use case");
  }

  @Override
  public Order assignOrder(Long orderId, Long employeeId) {
    throw new UnsupportedOperationException("Not supported in this use case");
  }

  public Order markReady(Long orderId) {
    Order order =
        orderPersistencePort
            .findById(orderId)
            .orElseThrow(() -> new DomainException("Order not found"));
    if (order.getStatus() != OrderStatus.EN_PREPARACION) {
      throw new DomainException("Only EN_PREPARACION orders can be marked as LISTO");
    }
    String previous = order.getStatus().name();
    order.setStatus(OrderStatus.LISTO);
    order.setPin(generatePin());
    Order saved = orderPersistencePort.save(order);
    eventPublisherPort.publishOrderStatusChanged(saved, previous);
    return saved;
  }

  private String generatePin() {
    int pin = 100000 + random.nextInt(900000);
    return String.valueOf(pin);
  }
}
