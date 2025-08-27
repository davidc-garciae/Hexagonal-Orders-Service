package com.pragma.powerup.infrastructure.out.messaging;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.spi.IOrderEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoOpOrderEventPublisherAdapter implements IOrderEventPublisherPort {

  @Override
  public void publishOrderStatusChanged(Order order, String previousStatus) {
    // Placeholder for future RabbitMQ/Kafka publisher; currently logs only.
    log.info(
        "Order status changed event (noop): orderId={}, previousStatus={}, newStatus={}, pin={}",
        order.getId(),
        previousStatus,
        order.getStatus(),
        order.getPin());
  }
}
