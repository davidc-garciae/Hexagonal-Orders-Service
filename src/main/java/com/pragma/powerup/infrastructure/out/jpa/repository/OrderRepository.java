package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
  boolean existsByCustomerIdAndStatusIn(
      Long customerId, java.util.Collection<com.pragma.powerup.domain.model.OrderStatus> statuses);

  Page<OrderEntity> findByRestaurantIdAndStatus(
      Long restaurantId, OrderStatus status, Pageable pageable);
}
