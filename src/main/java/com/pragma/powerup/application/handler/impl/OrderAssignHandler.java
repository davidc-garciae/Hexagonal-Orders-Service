package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderAssignHandler;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IAssignOrderServicePort;
import com.pragma.powerup.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderAssignHandler implements IOrderAssignHandler {

  private final IAssignOrderServicePort assignOrderServicePort;
  private final IOrderResponseMapper responseMapper;

  @Override
  public OrderResponseDto assign(Long orderId, Long employeeId) {
    Order updated = assignOrderServicePort.assignOrder(orderId, employeeId);
    return responseMapper.toResponse(updated);
  }
}
