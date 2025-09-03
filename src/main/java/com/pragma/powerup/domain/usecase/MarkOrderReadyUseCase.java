package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IMarkOrderReadyServicePort;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IMessagingFeignPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IUserFeignPort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.security.SecureRandom;
import java.time.LocalDateTime;

public class MarkOrderReadyUseCase implements IMarkOrderReadyServicePort {

  private final IOrderPersistencePort orderPersistencePort;
  private final IMessagingFeignPort messagingFeignPort;
  private final IUserFeignPort userFeignPort;
  private final ITraceabilityServicePort traceabilityServicePort;
  private final IUserServicePort userServicePort;
  private final SecureRandom random = new SecureRandom();

  public MarkOrderReadyUseCase(
      IOrderPersistencePort orderPersistencePort,
      IMessagingFeignPort messagingFeignPort,
      IUserFeignPort userFeignPort,
      ITraceabilityServicePort traceabilityServicePort,
      IUserServicePort userServicePort) {
    this.orderPersistencePort = orderPersistencePort;
    this.messagingFeignPort = messagingFeignPort;
    this.userFeignPort = userFeignPort;
    this.traceabilityServicePort = traceabilityServicePort;
    this.userServicePort = userServicePort;
  }

  @Override
  public Order markReady(Long orderId, Long employeeId) {
    Order order =
        orderPersistencePort
            .findById(orderId)
            .orElseThrow(() -> new DomainException("Order not found"));
    if (order.getStatus() != OrderStatus.EN_PREPARACION) {
      throw new DomainException("Only EN_PREPARACION orders can be marked as LISTO");
    }

    // Validate employee belongs to the restaurant that owns the order
    if (!userServicePort.isEmployeeOfRestaurant(employeeId, order.getRestaurantId())) {
      throw new DomainException(
          "Employee is not authorized to mark orders from this restaurant as ready");
    }

    // Get employee name for traceability
    String employeeName = userServicePort.getUserName(employeeId);

    String previousStatus = order.getStatus().name();
    order.setStatus(OrderStatus.LISTO);
    order.setPin(generatePin());
    Order saved = orderPersistencePort.save(order);

    UserModel customer = userFeignPort.getUserById(saved.getCustomerId());
    String message = "Your order #" + saved.getId() + " is ready. PIN: " + saved.getPin();
    messagingFeignPort.sendSms(customer.getPhone(), message);

    // Register traceability event using internal service
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

  private String generatePin() {
    int pin = 100000 + random.nextInt(900000);
    return String.valueOf(pin);
  }
}
