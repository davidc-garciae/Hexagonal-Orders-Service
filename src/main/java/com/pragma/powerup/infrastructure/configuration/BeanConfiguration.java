package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IAssignOrderServicePort;
import com.pragma.powerup.domain.api.ICancelOrderServicePort;
import com.pragma.powerup.domain.api.ICreateOrderServicePort;
import com.pragma.powerup.domain.api.IDeliverOrderServicePort;
import com.pragma.powerup.domain.api.IListOrdersServicePort;
import com.pragma.powerup.domain.api.IMarkOrderReadyServicePort;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.spi.IMessagingFeignPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.IUserFeignPort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import com.pragma.powerup.domain.usecase.AssignOrderUseCase;
import com.pragma.powerup.domain.usecase.CancelOrderUseCase;
import com.pragma.powerup.domain.usecase.CreateOrderUseCase;
import com.pragma.powerup.domain.usecase.DeliverOrderUseCase;
import com.pragma.powerup.domain.usecase.ListOrdersByStatusUseCase;
import com.pragma.powerup.domain.usecase.MarkOrderReadyUseCase;
import com.pragma.powerup.domain.usecase.TraceabilityUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  private final IOrderPersistencePort orderPersistencePort;
  private final IUserFeignPort userFeignPort;
  private final IMessagingFeignPort messagingFeignPort;
  private final ITraceabilityPersistencePort traceabilityPersistencePort;
  private final IUserServicePort userServicePort;

  public BeanConfiguration(
      IOrderPersistencePort orderPersistencePort,
      IUserFeignPort userFeignPort,
      IMessagingFeignPort messagingFeignPort,
      ITraceabilityPersistencePort traceabilityPersistencePort,
      IUserServicePort userServicePort) {
    this.orderPersistencePort = orderPersistencePort;
    this.userFeignPort = userFeignPort;
    this.messagingFeignPort = messagingFeignPort;
    this.traceabilityPersistencePort = traceabilityPersistencePort;
    this.userServicePort = userServicePort;
  }

  @Bean
  public ICreateOrderServicePort createOrderServicePort() {
    return new CreateOrderUseCase(orderPersistencePort, userFeignPort, traceabilityPersistencePort);
  }

  @Bean
  public IAssignOrderServicePort assignOrderServicePort() {
    return new AssignOrderUseCase(
        orderPersistencePort, userFeignPort, traceabilityServicePort(), userServicePort);
  }

  @Bean
  public IMarkOrderReadyServicePort markOrderReadyServicePort() {
    return new MarkOrderReadyUseCase(
        orderPersistencePort,
        messagingFeignPort,
        userFeignPort,
        traceabilityServicePort(),
        userServicePort);
  }

  @Bean
  public IDeliverOrderServicePort deliverOrderServicePort() {
    return new DeliverOrderUseCase(
        orderPersistencePort, traceabilityServicePort(), userServicePort);
  }

  @Bean
  public ICancelOrderServicePort cancelOrderServicePort() {
    return new CancelOrderUseCase(orderPersistencePort, userFeignPort, traceabilityServicePort());
  }

  @Bean
  public IListOrdersServicePort listOrdersServicePort() {
    return new ListOrdersByStatusUseCase(orderPersistencePort);
  }

  @Bean
  public ITraceabilityServicePort traceabilityServicePort() {
    return new TraceabilityUseCase(traceabilityPersistencePort, userServicePort);
  }
}
