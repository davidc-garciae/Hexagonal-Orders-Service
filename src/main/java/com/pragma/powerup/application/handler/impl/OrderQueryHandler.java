package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.OrderPageResponseDto;
import com.pragma.powerup.application.handler.IOrderQueryHandler;
import com.pragma.powerup.application.mapper.IOrderPageResponseMapper;
import com.pragma.powerup.domain.api.IListOrdersServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryHandler implements IOrderQueryHandler {

  private final IListOrdersServicePort listOrdersServicePort;
  private final IOrderPageResponseMapper pageMapper;

  @Override
  public OrderPageResponseDto listByStatusAndRestaurant(
      Long restaurantId, String status, int page, int size) {
    OrderStatus st;
    try {
      st = OrderStatus.valueOf(status);
    } catch (Exception e) {
      throw new DomainException("invalid status");
    }
    PagedResult<Order> result =
        listOrdersServicePort.listByStatusAndRestaurant(restaurantId, st, page, size);
    return pageMapper.toResponse(result);
  }

  @Override
  public OrderPageResponseDto listByCustomer(Long customerId, String status, int page, int size) {
    OrderStatus st = null;
    if (status != null && !status.trim().isEmpty()) {
      try {
        st = OrderStatus.valueOf(status);
      } catch (Exception e) {
        throw new DomainException("invalid status");
      }
    }
    PagedResult<Order> result = listOrdersServicePort.listByCustomer(customerId, st, page, size);
    return pageMapper.toResponse(result);
  }
}
