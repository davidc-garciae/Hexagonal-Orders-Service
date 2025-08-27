package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.usecase.CreateOrderUseCase;
import com.pragma.powerup.domain.usecase.ListOrdersByStatusUseCase;
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
}
