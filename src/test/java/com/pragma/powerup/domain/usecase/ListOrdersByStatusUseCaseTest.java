package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static com.pragma.powerup.testdata.TestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListOrdersByStatusUseCase Domain Tests")
class ListOrdersByStatusUseCaseTest {

  @Mock
  private IOrderPersistencePort orderPersistencePort;

  private ListOrdersByStatusUseCase listOrdersByStatusUseCase;

  @BeforeEach
  void setUp() {
    listOrdersByStatusUseCase = new ListOrdersByStatusUseCase(orderPersistencePort);
  }

  @Test
  @DisplayName("Should list orders by restaurant and status successfully")
  void listByStatusAndRestaurant_WithValidParameters_ShouldReturnPagedOrders() {
    // Given
    Order pendingOrder1 = orderWithStatus(STATUS_PENDING);
    Order pendingOrder2 = orderWithStatus(STATUS_PENDING);
    PagedResult<Order> expectedResult = new PagedResult<>(
        Arrays.asList(pendingOrder1, pendingOrder2),
        0, 10, 2, 1);

    when(orderPersistencePort.findByRestaurantAndStatus(RESTAURANT_ID, STATUS_PENDING, 0, 10))
        .thenReturn(expectedResult);

    // When
    PagedResult<Order> result = listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_PENDING, 0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getPage()).isZero();
    assertThat(result.getSize()).isEqualTo(10);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);

    verify(orderPersistencePort).findByRestaurantAndStatus(RESTAURANT_ID, STATUS_PENDING, 0, 10);
  }

  @Test
  @DisplayName("Should return empty page when no orders found")
  void listByStatusAndRestaurant_WithNoOrdersFound_ShouldReturnEmptyPage() {
    // Given
    PagedResult<Order> emptyResult = new PagedResult<>(
        Collections.emptyList(),
        0, 10, 0, 0);

    when(orderPersistencePort.findByRestaurantAndStatus(RESTAURANT_ID, STATUS_IN_PREPARATION, 0, 10))
        .thenReturn(emptyResult);

    // When
    PagedResult<Order> result = listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_IN_PREPARATION, 0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();

    verify(orderPersistencePort).findByRestaurantAndStatus(RESTAURANT_ID, STATUS_IN_PREPARATION, 0, 10);
  }

  @Test
  @DisplayName("Should throw exception when restaurant ID is null")
  void listByStatusAndRestaurant_WithNullRestaurantId_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByStatusAndRestaurant(
        null, STATUS_PENDING, 0, 10))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("restaurantId is required");

    verifyNoInteractions(orderPersistencePort);
  }

  @Test
  @DisplayName("Should throw exception when status is null")
  void listByStatusAndRestaurant_WithNullStatus_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, null, 0, 10))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("status is required");

    verifyNoInteractions(orderPersistencePort);
  }

  @Test
  @DisplayName("Should throw exception when page is negative")
  void listByStatusAndRestaurant_WithNegativePage_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_PENDING, -1, 10))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("invalid pagination parameters");

    verifyNoInteractions(orderPersistencePort);
  }

  @Test
  @DisplayName("Should throw exception when size is zero or negative")
  void listByStatusAndRestaurant_WithInvalidSize_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_PENDING, 0, 0))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("invalid pagination parameters");

    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_PENDING, 0, -5))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("invalid pagination parameters");

    verifyNoInteractions(orderPersistencePort);
  }

  @Test
  @DisplayName("Should list orders by customer with status successfully")
  void listByCustomer_WithStatus_ShouldReturnPagedOrders() {
    // Given
    Order customerOrder1 = orderWithStatus(STATUS_READY);
    Order customerOrder2 = orderWithStatus(STATUS_READY);
    PagedResult<Order> expectedResult = new PagedResult<>(
        Arrays.asList(customerOrder1, customerOrder2),
        0, 10, 2, 1);

    when(orderPersistencePort.findByCustomerAndStatus(CUSTOMER_ID, STATUS_READY, 0, 10))
        .thenReturn(expectedResult);

    // When
    PagedResult<Order> result = listOrdersByStatusUseCase.listByCustomer(
        CUSTOMER_ID, STATUS_READY, 0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);

    verify(orderPersistencePort).findByCustomerAndStatus(CUSTOMER_ID, STATUS_READY, 0, 10);
  }

  @Test
  @DisplayName("Should list all orders by customer when status is null")
  void listByCustomer_WithoutStatus_ShouldReturnAllCustomerOrders() {
    // Given
    Order customerOrder1 = orderWithStatus(STATUS_PENDING);
    Order customerOrder2 = orderWithStatus(STATUS_DELIVERED);
    PagedResult<Order> expectedResult = new PagedResult<>(
        Arrays.asList(customerOrder1, customerOrder2),
        0, 10, 2, 1);

    when(orderPersistencePort.findByCustomer(CUSTOMER_ID, 0, 10))
        .thenReturn(expectedResult);

    // When
    PagedResult<Order> result = listOrdersByStatusUseCase.listByCustomer(
        CUSTOMER_ID, null, 0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);

    verify(orderPersistencePort).findByCustomer(CUSTOMER_ID, 0, 10);
    verify(orderPersistencePort, never()).findByCustomerAndStatus(any(), any(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("Should throw exception when customer ID is null")
  void listByCustomer_WithNullCustomerId_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByCustomer(
        null, STATUS_PENDING, 0, 10))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("customerId is required");

    verifyNoInteractions(orderPersistencePort);
  }

  @Test
  @DisplayName("Should throw exception when pagination parameters are invalid for customer listing")
  void listByCustomer_WithInvalidPagination_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByCustomer(
        CUSTOMER_ID, STATUS_PENDING, -1, 10))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("invalid pagination parameters");

    assertThatThrownBy(() -> listOrdersByStatusUseCase.listByCustomer(
        CUSTOMER_ID, STATUS_PENDING, 0, 0))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("invalid pagination parameters");

    verifyNoInteractions(orderPersistencePort);
  }

  @Test
  @DisplayName("Should handle large page sizes correctly")
  void listByStatusAndRestaurant_WithLargePageSize_ShouldHandleCorrectly() {
    // Given
    PagedResult<Order> expectedResult = new PagedResult<>(
        Collections.emptyList(),
        0, 100, 0, 0);

    when(orderPersistencePort.findByRestaurantAndStatus(RESTAURANT_ID, STATUS_CANCELLED, 0, 100))
        .thenReturn(expectedResult);

    // When
    PagedResult<Order> result = listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_CANCELLED, 0, 100);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getSize()).isEqualTo(100);

    verify(orderPersistencePort).findByRestaurantAndStatus(RESTAURANT_ID, STATUS_CANCELLED, 0, 100);
  }

  @Test
  @DisplayName("Should handle different order statuses correctly")
  void listByStatusAndRestaurant_WithDifferentStatuses_ShouldHandleCorrectly() {
    // Given
    PagedResult<Order> deliveredResult = new PagedResult<>(
        Collections.singletonList(orderWithStatus(STATUS_DELIVERED)),
        0, 10, 1, 1);

    when(orderPersistencePort.findByRestaurantAndStatus(RESTAURANT_ID, STATUS_DELIVERED, 0, 10))
        .thenReturn(deliveredResult);

    // When
    PagedResult<Order> result = listOrdersByStatusUseCase.listByStatusAndRestaurant(
        RESTAURANT_ID, STATUS_DELIVERED, 0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(STATUS_DELIVERED);

    verify(orderPersistencePort).findByRestaurantAndStatus(RESTAURANT_ID, STATUS_DELIVERED, 0, 10);
  }
}
