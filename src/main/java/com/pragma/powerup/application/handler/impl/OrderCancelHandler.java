package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderCancelHandler;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.ICancelOrderServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCancelHandler implements IOrderCancelHandler {

  private final ICancelOrderServicePort cancelOrderServicePort;
  private final IOrderResponseMapper responseMapper;

  @Override
  public OrderResponseDto cancel(Long orderId, Long customerId) {
    var updated = cancelOrderServicePort.cancel(orderId, customerId);
    return responseMapper.toResponse(updated);
  }
}
