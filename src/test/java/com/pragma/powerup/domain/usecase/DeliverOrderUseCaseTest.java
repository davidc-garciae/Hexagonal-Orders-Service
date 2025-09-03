package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.TraceabilityEvent;
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
@DisplayName("DeliverOrderUseCase Domain Tests")
class DeliverOrderUseCaseTest {

  @Mock
  private IOrderPersistencePort orderPersistencePort;

  @Mock
  private ITraceabilityServicePort traceabilityServicePort;

  @Mock
  private IUserServicePort userServicePort;

  private DeliverOrderUseCase deliverOrderUseCase;

  private static final String VALID_PIN = "123456";
  private static final String INVALID_PIN = "654321";

  @BeforeEach
  void setUp() {
    deliverOrderUseCase = new DeliverOrderUseCase(
        orderPersistencePort,
        traceabilityServicePort,
        userServicePort);
  }

  @Test
  @DisplayName("Should deliver order successfully with valid PIN")
  void deliver_WithValidPin_ShouldDeliverOrder() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin(VALID_PIN);
    Order deliveredOrder = orderDelivered();
    deliveredOrder.setId(ORDER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(deliveredOrder);

    // When
    Order result = deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(STATUS_DELIVERED);

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    verify(userServicePort).getUserName(EMPLOYEE_ID);
    verify(orderPersistencePort).save(any(Order.class));
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should throw exception when order not found")
  void deliver_WithNonExistentOrder_ShouldThrowException() {
    // Given
    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Order not found");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when order is not ready")
  void deliver_WithNonReadyOrder_ShouldThrowException() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));

    // When & Then
    assertThatThrownBy(() -> deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Only LISTO orders can be delivered");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception with invalid PIN")
  void deliver_WithInvalidPin_ShouldThrowException() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin(VALID_PIN);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));

    // When & Then
    assertThatThrownBy(() -> deliverOrderUseCase.deliver(ORDER_ID, INVALID_PIN, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Invalid PIN");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception with null PIN")
  void deliver_WithNullPin_ShouldThrowException() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin(null);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));

    // When & Then
    assertThatThrownBy(() -> deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Invalid PIN");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when employee is not from restaurant")
  void deliver_WithUnauthorizedEmployee_ShouldThrowException() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin(VALID_PIN);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Employee is not authorized");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    verifyNoMoreInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should create traceability event when order is delivered")
  void deliver_ShouldCreateTraceabilityEvent() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin(VALID_PIN);
    Order deliveredOrder = orderDelivered();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(deliveredOrder);

    // When
    deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID);

    // Then
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should update order status to ENTREGADO when delivered")
  void deliver_ShouldUpdateStatusToDelivered() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin(VALID_PIN);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenAnswer(invocation -> {
      Order savedOrder = invocation.getArgument(0);
      assertThat(savedOrder.getStatus()).isEqualTo(STATUS_DELIVERED);
      return savedOrder;
    });

    // When
    deliverOrderUseCase.deliver(ORDER_ID, VALID_PIN, EMPLOYEE_ID);

    // Then
    verify(orderPersistencePort).save(any(Order.class));
  }

  @Test
  @DisplayName("Should validate PIN is case sensitive")
  void deliver_WithWrongCasePin_ShouldThrowException() {
    // Given
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin("123456");

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));

    // When & Then
    assertThatThrownBy(() -> deliverOrderUseCase.deliver(ORDER_ID, "123457", EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Invalid PIN");
  }
}
