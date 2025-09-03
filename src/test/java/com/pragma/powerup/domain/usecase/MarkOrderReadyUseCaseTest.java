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
@DisplayName("MarkOrderReadyUseCase Domain Tests")
class MarkOrderReadyUseCaseTest {

  @Mock
  private IOrderPersistencePort orderPersistencePort;

  @Mock
  private IMessagingFeignPort messagingFeignPort;

  @Mock
  private IUserFeignPort userFeignPort;

  @Mock
  private ITraceabilityServicePort traceabilityServicePort;

  @Mock
  private IUserServicePort userServicePort;

  private MarkOrderReadyUseCase markOrderReadyUseCase;

  @BeforeEach
  void setUp() {
    markOrderReadyUseCase = new MarkOrderReadyUseCase(
        orderPersistencePort,
        messagingFeignPort,
        userFeignPort,
        traceabilityServicePort,
        userServicePort);
  }

  @Test
  @DisplayName("Should mark order ready successfully")
  void markReady_WithValidOrder_ShouldMarkOrderReady() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);
    UserModel customer = validCustomer();
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin("123456");

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(readyOrder);
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When
    Order result = markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(STATUS_READY);
    assertThat(result.getPin()).isNotEmpty();

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    verify(userServicePort).getUserName(EMPLOYEE_ID);
    verify(orderPersistencePort).save(any(Order.class));
    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verify(messagingFeignPort).sendSms(eq(customer.getPhone()), anyString());
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should throw exception when order not found")
  void markReady_WithNonExistentOrder_ShouldThrowException() {
    // Given
    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Order not found");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(messagingFeignPort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when order is not in preparation")
  void markReady_WithNonPreparationOrder_ShouldThrowException() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));

    // When & Then
    assertThatThrownBy(() -> markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Only EN_PREPARACION orders can be marked as LISTO");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(messagingFeignPort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when employee is not from restaurant")
  void markReady_WithUnauthorizedEmployee_ShouldThrowException() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Employee is not authorized");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    verifyNoMoreInteractions(userServicePort);
    verifyNoInteractions(messagingFeignPort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should send SMS notification to customer")
  void markReady_ShouldSendSmsNotification() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);
    UserModel customer = validCustomer();
    Order readyOrder = orderReady();
    readyOrder.setId(ORDER_ID);
    readyOrder.setPin("123456");

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(readyOrder);
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When
    markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID);

    // Then
    verify(messagingFeignPort).sendSms(eq(customer.getPhone()), contains("PIN: 123456"));
  }

  @Test
  @DisplayName("Should generate PIN when marking order ready")
  void markReady_ShouldGeneratePin() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);
    UserModel customer = validCustomer();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When
    Order result = markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID);

    // Then
    assertThat(result.getPin()).isNotNull();
    assertThat(result.getPin()).hasSize(6);
    assertThat(result.getPin()).matches("\\d{6}");
  }

  @Test
  @DisplayName("Should create traceability event when order is marked ready")
  void markReady_ShouldCreateTraceabilityEvent() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);
    UserModel customer = validCustomer();
    Order readyOrder = orderReady();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(readyOrder);
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When
    markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID);

    // Then
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should update order status to LISTO")
  void markReady_ShouldUpdateStatusToReady() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);
    UserModel customer = validCustomer();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenAnswer(invocation -> {
      Order savedOrder = invocation.getArgument(0);
      assertThat(savedOrder.getStatus()).isEqualTo(STATUS_READY);
      return savedOrder;
    });
    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);

    // When
    markOrderReadyUseCase.markReady(ORDER_ID, EMPLOYEE_ID);

    // Then
    verify(orderPersistencePort).save(any(Order.class));
  }
}
