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
@DisplayName("AssignOrderUseCase Domain Tests")
class AssignOrderUseCaseTest {

  @Mock
  private IOrderPersistencePort orderPersistencePort;

  @Mock
  private IUserFeignPort userFeignPort;

  @Mock
  private ITraceabilityServicePort traceabilityServicePort;

  @Mock
  private IUserServicePort userServicePort;

  private AssignOrderUseCase assignOrderUseCase;

  @BeforeEach
  void setUp() {
    assignOrderUseCase = new AssignOrderUseCase(
        orderPersistencePort,
        userFeignPort,
        traceabilityServicePort,
        userServicePort);
  }

  @Test
  @DisplayName("Should assign order successfully to authorized employee")
  void assignOrder_WithAuthorizedEmployee_ShouldAssignOrder() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    UserModel employee = validEmployee();
    Order assignedOrder = orderInPreparation();
    assignedOrder.setId(ORDER_ID);
    assignedOrder.setEmployeeId(EMPLOYEE_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(EMPLOYEE_ID)).thenReturn(employee);
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(assignedOrder);

    // When
    Order result = assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(STATUS_IN_PREPARATION);
    assertThat(result.getEmployeeId()).isEqualTo(EMPLOYEE_ID);

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(EMPLOYEE_ID);
    verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    verify(userServicePort).getUserName(EMPLOYEE_ID);
    verify(orderPersistencePort).save(any(Order.class));
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should throw exception when order not found")
  void assignOrder_WithNonExistentOrder_ShouldThrowException() {
    // Given
    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Order not found");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when order is not pending")
  void assignOrder_WithNonPendingOrder_ShouldThrowException() {
    // Given
    Order orderInPreparation = orderInPreparation();
    orderInPreparation.setId(ORDER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));

    // When & Then
    assertThatThrownBy(() -> assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Only PENDIENTE orders can be assigned");

    verify(orderPersistencePort).findById(ORDER_ID);
    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should throw exception when employee is not from restaurant")
  void assignOrder_WithUnauthorizedEmployee_ShouldThrowException() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    UserModel employee = validEmployee();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(EMPLOYEE_ID)).thenReturn(employee);
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Employee is not authorized");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(EMPLOYEE_ID);
    verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    verifyNoMoreInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should create traceability event when order is assigned")
  void assignOrder_ShouldCreateTraceabilityEvent() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    UserModel employee = validEmployee();
    Order assignedOrder = orderInPreparation();
    assignedOrder.setEmployeeId(EMPLOYEE_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(EMPLOYEE_ID)).thenReturn(employee);
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(assignedOrder);

    // When
    assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID);

    // Then
    verify(traceabilityServicePort).createTraceabilityEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should validate employee exists before assignment")
  void assignOrder_ShouldValidateEmployeeExists() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(EMPLOYEE_ID)).thenThrow(new DomainException("Employee not found"));

    // When & Then
    assertThatThrownBy(() -> assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Employee not found");

    verify(orderPersistencePort).findById(ORDER_ID);
    verify(userFeignPort).getUserById(EMPLOYEE_ID);
    verifyNoInteractions(userServicePort);
    verifyNoInteractions(traceabilityServicePort);
  }

  @Test
  @DisplayName("Should update order status to EN_PREPARACION when assigned")
  void assignOrder_ShouldUpdateStatusToInPreparation() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    UserModel employee = validEmployee();
    Order assignedOrder = orderInPreparation();
    assignedOrder.setEmployeeId(EMPLOYEE_ID);

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(EMPLOYEE_ID)).thenReturn(employee);
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(assignedOrder);

    // When
    Order result = assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID);

    // Then
    assertThat(result.getStatus()).isEqualTo(STATUS_IN_PREPARATION);
    assertThat(result.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
  }

  @Test
  @DisplayName("Should handle persistence failure during assignment")
  void assignOrder_WhenPersistenceFails_ShouldThrowException() {
    // Given
    Order pendingOrder = orderWithStatus(STATUS_PENDING);
    pendingOrder.setId(ORDER_ID);
    UserModel employee = validEmployee();

    when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
    when(userFeignPort.getUserById(EMPLOYEE_ID)).thenReturn(employee);
    when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);
    when(userServicePort.getUserName(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME);
    when(orderPersistencePort.save(any(Order.class)))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> assignOrderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Database error");

    verify(orderPersistencePort).save(any(Order.class));
    verifyNoInteractions(traceabilityServicePort);
  }
}
