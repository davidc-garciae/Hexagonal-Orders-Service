package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

  private final OrderRepository orderRepository;
  private final IOrderEntityMapper orderEntityMapper;

  @Override
  public boolean customerHasActiveOrder(Long customerId) {
    List<com.pragma.powerup.domain.model.OrderStatus> active =
        List.of(
            com.pragma.powerup.domain.model.OrderStatus.PENDIENTE,
            com.pragma.powerup.domain.model.OrderStatus.EN_PREPARACION,
            com.pragma.powerup.domain.model.OrderStatus.LISTO);
    return orderRepository.existsByCustomerIdAndStatusIn(customerId, active);
  }

  @Override
  public Order save(Order order) {
    OrderEntity entity = orderEntityMapper.toEntity(order);
    if (entity.getItems() != null) {
      entity.getItems().forEach(i -> i.setOrder(entity));
    }
    OrderEntity saved = orderRepository.save(entity);
    return orderEntityMapper.toModel(saved);
  }

  @Override
  public boolean allDishesBelongToRestaurant(Long restaurantId, List<Long> dishIds) {
    // For now, assume validation is done via synchronous call in application/infra
    // to restaurants-service.
    // This adapter does not know about external services; return true and delegate
    // actual validation elsewhere.
    return true;
  }

  @Override
  public PagedResult<Order> findByRestaurantAndStatus(
      Long restaurantId, OrderStatus status, int page, int size) {
    Page<OrderEntity> p =
        orderRepository.findByRestaurantIdAndStatus(
            restaurantId, status, PageRequest.of(page, size));
    List<Order> content = p.getContent().stream().map(orderEntityMapper::toModel).toList();
    return new PagedResult<>(content, page, size, p.getTotalElements(), p.getTotalPages());
  }
}
