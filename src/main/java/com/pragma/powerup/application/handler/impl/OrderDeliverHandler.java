package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderDeliverRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderDeliverHandler;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.usecase.DeliverOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDeliverHandler implements IOrderDeliverHandler {

  private final DeliverOrderUseCase deliverOrderUseCase;
  private final IOrderResponseMapper responseMapper;

  @Override
  public OrderResponseDto deliver(Long orderId, OrderDeliverRequestDto request) {
    var updated = deliverOrderUseCase.deliver(orderId, request.getPin());
    return responseMapper.toResponse(updated);
  }
}
