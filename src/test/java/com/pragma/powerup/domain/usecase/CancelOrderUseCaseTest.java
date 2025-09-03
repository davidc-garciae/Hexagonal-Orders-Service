package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.pragma.powerup.testdata.TestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOrderUseCase Domain Tests")
class CancelOrderUseCaseTest {

  @Mock
  private IOrderPersistencePort orderPersistencePort;

  @Mock
  private IUserFeignPort userFeignPort;

  @Mock
  private ITraceabilityServicePort traceabilityServicePort;

  private CancelOrderUseCase cancelOrderUseCase;

  @BeforeEach
  void setUp() {
    cancelOrderUseCase = new CancelOrderUseCase(
        orderPersistencePort,
        userFeignPort,
        traceabilityServicePort);
  }

  @Test
  @DisplayName("Should cancel order successfully when customer is authorized")
  void cancel_WithAuthorizedCustomer_ShouldCancelOrder() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    pendingOrder.setCustomerId(CUSTOMER_ID);
    UserModel customer = validCustomer();
    Order cancelledOrder = orderCancelled();
    cancelledOrder.setId(ORDER_ID);
    cancelledOrder.setCustomerId(CUSTOMER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(cancelledOrder);

    // When
    Order result = cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(STATUS_CANCELLED);
    assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verify(orderPersistencePort).save(any(Order.class));
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should throw exception when order not found")
  void cancel_WithNonExistentOrder_ShouldThrowException() {
    // Given
    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Order not found");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when customer is not order owner")
  void cancel_WithUnauthorizedCustomer_ShouldThrowException() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    pendingOrder.setCustomerId(CUSTOMER_ID);
    UserModel otherCustomer = validCustomer();
    Long otherCustomerId = 999L;

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(otherCustomerId)).thenReturn(otherCustomer);

    // When & Then
    assertThatThrownBy(() -> cancelOrderUseCase.cancel(ORDER_ID, otherCustomerId))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Forbidden");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(otherCustomerId);
    verifyNoMoreInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when order is not pending")
  void cancel_WithNonPendingOrder_ShouldThrowException() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);
    orderInPreparation.setCustomerId(CUSTOMER_ID);
    UserModel customer = validCustomer();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When & Then
    assertThatThrownBy(() -> cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("ya está en preparación y no puede cancelarse");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verifyNoMoreInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should create traceability event when order is cancelled")
  void cancel_ShouldCreateTraceabilityEvent() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    pendingOrder.setCustomerId(CUSTOMER_ID);
    UserModel customer = validCustomer();
    Order cancelledOrder = orderCancelled();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(cancelledOrder);

    // When
    cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID);

    // Then
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should update order status to CANCELADO when cancelled")
  void cancel_ShouldUpdateStatusToCancelled() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    pendingOrder.setCustomerId(CUSTOMER_ID);
    UserModel customer = validCustomer();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.save(any(Order.class))).thenAnswer(invocation -> {
      Order savedOrder = invocation.getArgument(0);
      assertThat(savedOrder.getStatus()).isEqualTo(STATUS_CANCELLED);
      return savedOrder;
    });

    // When
    cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID);

    // Then
    verify(orderPersistencePort).save(any(Order.class));
  }

  @Test
  @DisplayName("Should validate customer exists before cancelling")
  void cancel_ShouldValidateCustomerExists() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    pendingOrder.setCustomerId(CUSTOMER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenThrow(new DomainException("Customer not found"));

    // When & Then
    assertThatThrownBy(() -> cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Customer not found");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verifyNoMoreInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should not cancel ready orders")
  void cancel_WithReadyOrder_ShouldThrowException() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setCustomerId(CUSTOMER_ID);
    UserModel customer = validCustomer();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When & Then
    assertThatThrownBy(() -> cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("ya está en preparación y no puede cancelarse");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verifyNoMoreInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should not cancel delivered orders")
  void cancel_WithDeliveredOrder_ShouldThrowException() {
    // Given
    Order deliveredOrder = orderDelivered();
    deliveredOrder.setId(ORDER_ID);
    deliveredOrder.setCustomerId(CUSTOMER_ID);
    UserModel customer = validCustomer();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(deliveredOrder));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When & Then
    assertThatThrownBy(() -> cancelOrderUseCase.cancel(ORDER_ID, CUSTOMER_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("ya está en preparación y no puede cancelarse");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verifyNoMoreInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityServicePort);
  }
}
