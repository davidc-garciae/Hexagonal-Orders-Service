package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderReadyHandler;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IMarkOrderReadyServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderReadyHandler implements IOrderReadyHandler {

  private final IMarkOrderReadyServicePort markOrderReadyServicePort;
  private final IOrderResponseMapper responseMapper;

  @Override
  public OrderResponseDto markReady(Long orderId, Long employeeId) {
    var order = markOrderReadyServicePort.markReady(orderId, employeeId);
    return responseMapper.toResponse(order);
  }
}
