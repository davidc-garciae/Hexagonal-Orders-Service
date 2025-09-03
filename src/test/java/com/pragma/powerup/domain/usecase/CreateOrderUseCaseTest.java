package com.pragma.powerup.domain.usecase;

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

import static com.pragma.powerup.testdata.TestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderUseCase Domain Tests")
class CreateOrderUseCaseTest {

  @Mock
  private IOrderPersistencePort orderPersistencePort;

  @Mock
  private IUserFeignPort userFeignPort;

  @Mock
  private ITraceabilityPersistencePort traceabilityPersistencePort;

  private CreateOrderUseCase createOrderUseCase;

  @BeforeEach
  void setUp() {
    createOrderUseCase = new CreateOrderUseCase(
        orderPersistencePort,
        userFeignPort,
        traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should create order successfully with valid request")
  void createOrder_WithValidRequest_ShouldCreateOrder() {
    // Given
    Order order = validOrder();
    UserModel customer = validCustomer();
    Order savedOrder = orderWithId(1L);

    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.customerHasActiveOrder(CUSTOMER_ID)).thenReturn(false);
    when(orderPersistencePort.allDishesBelongToRestaurant(eq(RESTAURANT_ID), anyList())).thenReturn(true);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(savedOrder);

    // When
    Order result = createOrderUseCase.createOrder(order);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getStatus()).isEqualTo(STATUS_PENDING);

    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verify(orderPersistencePort).customerHasActiveOrder(CUSTOMER_ID);
    verify(orderPersistencePort).allDishesBelongToRestaurant(eq(RESTAURANT_ID), anyList());
    verify(orderPersistencePort).save(any(Order.class));
    verify(traceabilityPersistencePort).saveEvent(any(TraceabilityEvent.class));
  }

  @Test
  @DisplayName("Should throw exception when customer has active order")
  void createOrder_WithActiveOrder_ShouldThrowException() {
    // Given
    Order order = validOrder();
    UserModel customer = validCustomer();

    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.customerHasActiveOrder(CUSTOMER_ID)).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(order))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("active order");

    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verify(orderPersistencePort).customerHasActiveOrder(CUSTOMER_ID);
    verifyNoMoreInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should throw exception when dishes don't belong to restaurant")
  void createOrder_WithInvalidDishes_ShouldThrowException() {
    // Given
    Order order = validOrder();
    UserModel customer = validCustomer();

    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.customerHasActiveOrder(CUSTOMER_ID)).thenReturn(false);
    when(orderPersistencePort.allDishesBelongToRestaurant(eq(RESTAURANT_ID), anyList())).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(order))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("dishes must belong");

    verify(userFeignPort).getUserById(CUSTOMER_ID);
    verify(orderPersistencePort).customerHasActiveOrder(CUSTOMER_ID);
    verify(orderPersistencePort).allDishesBelongToRestaurant(eq(RESTAURANT_ID), anyList());
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should validate null order request")
  void createOrder_WithNullOrder_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Order is required");

    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should validate missing customer ID")
  void createOrder_WithoutCustomerId_ShouldThrowException() {
    // Given
    Order order = validOrder();
    order.setCustomerId(null);

    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(order))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("customerId is required");

    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should validate missing restaurant ID")
  void createOrder_WithoutRestaurantId_ShouldThrowException() {
    // Given
    Order order = validOrder();
    order.setRestaurantId(null);

    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(order))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("restaurantId is required");

    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should validate empty items list")
  void createOrder_WithEmptyItems_ShouldThrowException() {
    // Given
    Order order = validOrder();
    order.setItems(emptyItemsList());

    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(order))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("items must not be empty");

    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should validate items with invalid quantity")
  void createOrder_WithInvalidItemQuantity_ShouldThrowException() {
    // Given
    Order order = validOrder();
    order.setItems(invalidQuantityItems());

    // When & Then
    assertThatThrownBy(() -> createOrderUseCase.createOrder(order))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("quantity > 0");

    verifyNoInteractions(userFeignPort);
    verifyNoInteractions(orderPersistencePort);
    verifyNoInteractions(traceabilityPersistencePort);
  }

  @Test
  @DisplayName("Should create traceability event on successful order creation")
  void createOrder_ShouldCreateTraceabilityEvent() {
    // Given
    Order order = validOrder();
    UserModel customer = validCustomer();
    Order savedOrder = orderWithId(1L);

    when(userFeignPort.getUserById(CUSTOMER_ID)).thenReturn(customer);
    when(orderPersistencePort.customerHasActiveOrder(CUSTOMER_ID)).thenReturn(false);
    when(orderPersistencePort.allDishesBelongToRestaurant(eq(RESTAURANT_ID), anyList())).thenReturn(true);
    when(orderPersistencePort.save(any(Order.class))).thenReturn(savedOrder);

    // When
    createOrderUseCase.createOrder(order);

    // Then
    verify(traceabilityPersistencePort).saveEvent(any(TraceabilityEvent.class));
  }
}
