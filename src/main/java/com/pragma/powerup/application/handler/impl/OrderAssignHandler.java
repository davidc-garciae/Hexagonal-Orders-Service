package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderAssignHandler;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderAssignHandler implements IOrderAssignHandler {

    @org.springframework.beans.factory.annotation.Qualifier("orderAssignServicePort")
    private final IOrderServicePort orderServicePort;

    private final IOrderResponseMapper responseMapper;

    @Override
    public OrderResponseDto assign(Long orderId, Long employeeId) {
        Order updated = orderServicePort.assignOrder(orderId, employeeId);
        return responseMapper.toResponse(updated);
    }
}
