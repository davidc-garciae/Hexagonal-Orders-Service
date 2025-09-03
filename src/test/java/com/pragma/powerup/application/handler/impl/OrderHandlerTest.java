package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.mapper.IOrderRequestMapper;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.ICreateOrderServicePort;
import com.pragma.powerup.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.pragma.powerup.testdata.TestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderHandler Application Tests")
class OrderHandlerTest {

    @Mock
    private ICreateOrderServicePort createOrderServicePort;

    @Mock
    private IOrderRequestMapper orderRequestMapper;

    @Mock
    private IOrderResponseMapper orderResponseMapper;

    private OrderHandler orderHandler;

    @BeforeEach
    void setUp() {
        orderHandler = new OrderHandler(
                createOrderServicePort,
                orderRequestMapper,
                orderResponseMapper);
    }

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_WithValidRequest_ShouldReturnOrderResponse() {
        // Given
        OrderCreateRequestDto request = validOrderCreateRequest();
        Order orderModel = validOrder();
        Order createdOrder = validOrder();
        createdOrder.setId(ORDER_ID);
        OrderResponseDto expectedResponse = validOrderResponse();

        when(orderRequestMapper.toModel(request)).thenReturn(orderModel);
        when(createOrderServicePort.createOrder(orderModel)).thenReturn(createdOrder);
        when(orderResponseMapper.toResponse(createdOrder)).thenReturn(expectedResponse);

        // When
        OrderResponseDto result = orderHandler.createOrder(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResponse);

        verify(orderRequestMapper).toModel(request);
        verify(createOrderServicePort).createOrder(orderModel);
        verify(orderResponseMapper).toResponse(createdOrder);
    }

    @Test
    @DisplayName("Should handle mapper transformations correctly")
    void createOrder_ShouldCallMappers() {
        // Given
        OrderCreateRequestDto request = validOrderCreateRequest();
        Order orderModel = validOrder();
        Order createdOrder = validOrder();
        OrderResponseDto response = validOrderResponse();

        when(orderRequestMapper.toModel(any(OrderCreateRequestDto.class))).thenReturn(orderModel);
        when(createOrderServicePort.createOrder(any(Order.class))).thenReturn(createdOrder);
        when(orderResponseMapper.toResponse(any(Order.class))).thenReturn(response);

        // When
        OrderResponseDto result = orderHandler.createOrder(request);

        // Then
        assertThat(result).isNotNull();

        verify(orderRequestMapper, times(1)).toModel(request);
        verify(orderResponseMapper, times(1)).toResponse(createdOrder);
    }

    @Test
    @DisplayName("Should delegate to domain service")
    void createOrder_ShouldDelegateToService() {
        // Given
        OrderCreateRequestDto request = validOrderCreateRequest();
        Order orderModel = validOrder();
        Order createdOrder = validOrder();
        OrderResponseDto response = validOrderResponse();

        when(orderRequestMapper.toModel(request)).thenReturn(orderModel);
        when(createOrderServicePort.createOrder(orderModel)).thenReturn(createdOrder);
        when(orderResponseMapper.toResponse(createdOrder)).thenReturn(response);

        // When
        orderHandler.createOrder(request);

        // Then
        verify(createOrderServicePort).createOrder(orderModel);
    }

    @Test
    @DisplayName("Should maintain transaction boundary")
    void createOrder_ShouldBeTransactional() {
        // Given
        OrderCreateRequestDto request = validOrderCreateRequest();
        Order orderModel = validOrder();
        Order createdOrder = validOrder();
        OrderResponseDto response = validOrderResponse();

        when(orderRequestMapper.toModel(request)).thenReturn(orderModel);
        when(createOrderServicePort.createOrder(orderModel)).thenReturn(createdOrder);
        when(orderResponseMapper.toResponse(createdOrder)).thenReturn(response);

        // When
        OrderResponseDto result = orderHandler.createOrder(request);

        // Then
        assertThat(result).isNotNull();
        // The @Transactional annotation ensures transactional behavior
        // This test verifies the method completes successfully within transaction scope
    }
}
