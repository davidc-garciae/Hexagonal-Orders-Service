package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.spi.IOrderEventPublisherPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.usecase.AssignOrderUseCase;
import com.pragma.powerup.domain.usecase.CreateOrderUseCase;
import com.pragma.powerup.domain.usecase.DeliverOrderUseCase;
import com.pragma.powerup.domain.usecase.ListOrdersByStatusUseCase;
import com.pragma.powerup.domain.usecase.MarkOrderReadyUseCase;
import com.pragma.powerup.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

  private final OrderJpaAdapter orderJpaAdapter;

  @Bean
  public IOrderPersistencePort orderPersistencePort() {
    return orderJpaAdapter;
  }

  @Bean
  public IOrderServicePort orderServicePort() {
    return new CreateOrderUseCase(orderPersistencePort());
  }

  @Bean(name = "orderQueryServicePort")
  public IOrderServicePort orderQueryServicePort() {
    return new ListOrdersByStatusUseCase(orderPersistencePort());
  }

  @Bean(name = "orderAssignServicePort")
  public IOrderServicePort orderAssignServicePort() {
    return new AssignOrderUseCase(orderPersistencePort());
  }

  @Bean
  public MarkOrderReadyUseCase orderReadyUseCase(
      IOrderPersistencePort orderPersistencePort, IOrderEventPublisherPort eventPublisherPort) {
    return new MarkOrderReadyUseCase(orderPersistencePort, eventPublisherPort);
  }

  @Bean
  public DeliverOrderUseCase deliverOrderUseCase(IOrderPersistencePort orderPersistencePort) {
    return new DeliverOrderUseCase(orderPersistencePort);
  }
}
