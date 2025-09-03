package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderDeliverRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderDeliverHandler;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IDeliverOrderServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDeliverHandler implements IOrderDeliverHandler {

  private final IDeliverOrderServicePort deliverOrderServicePort;
  private final IOrderResponseMapper responseMapper;

  @Override
  public OrderResponseDto deliver(Long orderId, OrderDeliverRequestDto request, Long employeeId) {
    var updated = deliverOrderServicePort.deliver(orderId, request.getPin(), employeeId);
    return responseMapper.toResponse(updated);
  }
}
