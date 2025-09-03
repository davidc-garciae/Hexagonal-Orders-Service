package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.EmployeeMetrics;
import com.pragma.powerup.domain.model.RestaurantMetrics;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetRestaurantMetricsUseCaseTest {

  private final ITraceabilityPersistencePort traceabilityPersistencePort =
      mock(ITraceabilityPersistencePort.class);
  private final IUserServicePort userServicePort = mock(IUserServicePort.class);

  private ITraceabilityServicePort useCase;

  @BeforeEach
  void setUp() {
    useCase = new TraceabilityUseCase(traceabilityPersistencePort, userServicePort);
  }

  @Test
  @DisplayName("Should return restaurant metrics when owner requests their own restaurant")
  void shouldReturnMetricsWhenOwnerRequestsOwnRestaurant() {
    // Arrange
    Long restaurantId = 456L;
    Long ownerId = 100L;
    String userRole = "OWNER";
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 1, 31);

    EmployeeMetrics employee1 =
        EmployeeMetrics.builder()
            .employeeId(321L)
            .ordersProcessed(15)
            .averagePreparationTime(20)
            .averageDeliveryTime(35)
            .build();

    EmployeeMetrics employee2 =
        EmployeeMetrics.builder()
            .employeeId(322L)
            .ordersProcessed(12)
            .averagePreparationTime(25)
            .averageDeliveryTime(40)
            .build();

    List<EmployeeMetrics> employeeMetrics = Arrays.asList(employee1, employee2);

    when(userServicePort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);
    when(userServicePort.getRestaurantName(restaurantId)).thenReturn("La Pizzeria");
    when(traceabilityPersistencePort.countOrdersByRestaurant(restaurantId, startDate, endDate))
        .thenReturn(150);
    when(traceabilityPersistencePort.calculateAveragePreparationTime(
            restaurantId, startDate, endDate))
        .thenReturn(22);
    when(traceabilityPersistencePort.calculateAverageDeliveryTime(restaurantId, startDate, endDate))
        .thenReturn(37);
    when(traceabilityPersistencePort.calculateEmployeeMetrics(restaurantId, startDate, endDate))
        .thenReturn(employeeMetrics);

    // Act
    RestaurantMetrics result =
        useCase.getRestaurantMetrics(restaurantId, ownerId, userRole, startDate, endDate);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(
            metrics -> {
              assertThat(metrics.getRestaurantId()).isEqualTo(restaurantId);
              assertThat(metrics.getRestaurantName()).isEqualTo("La Pizzeria");
              assertThat(metrics.getTotalOrders()).isEqualTo(150);
              assertThat(metrics.getAveragePreparationTime()).isEqualTo(22);
              assertThat(metrics.getAverageDeliveryTime()).isEqualTo(37);
              assertThat(metrics.getPeriodStart()).isEqualTo(startDate);
              assertThat(metrics.getPeriodEnd()).isEqualTo(endDate);
              assertThat(metrics.getEmployeeRankings()).hasSize(2);
            });

    verify(userServicePort).isOwnerOfRestaurant(ownerId, restaurantId);
    verify(userServicePort).getRestaurantName(restaurantId);
    verify(traceabilityPersistencePort).countOrdersByRestaurant(restaurantId, startDate, endDate);
  }

  @Test
  @DisplayName("Should use default dates when start and end dates are null")
  void shouldUseDefaultDatesWhenNullProvided() {
    // Arrange
    Long restaurantId = 456L;
    Long ownerId = 100L;
    String userRole = "OWNER";

    when(userServicePort.isOwnerOfRestaurant(ownerId, restaurantId)).thenReturn(true);
    when(userServicePort.getRestaurantName(restaurantId)).thenReturn("La Pizzeria");
    when(traceabilityPersistencePort.countOrdersByRestaurant(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(50);
    when(traceabilityPersistencePort.calculateAveragePreparationTime(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(25);
    when(traceabilityPersistencePort.calculateAverageDeliveryTime(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(40);
    when(traceabilityPersistencePort.calculateEmployeeMetrics(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Arrays.asList());

    // Act
    RestaurantMetrics result =
        useCase.getRestaurantMetrics(restaurantId, ownerId, userRole, null, null);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getPeriodStart()).isNotNull();
    assertThat(result.getPeriodEnd()).isNotNull();
    assertThat(result.getPeriodStart()).isBefore(result.getPeriodEnd());

    verify(traceabilityPersistencePort)
        .countOrdersByRestaurant(eq(restaurantId), any(LocalDate.class), any(LocalDate.class));
  }

  @Test
  @DisplayName("Should throw exception when non-owner requests restaurant metrics")
  void shouldThrowExceptionWhenNonOwnerRequestsMetrics() {
    // Arrange
    Long restaurantId = 456L;
    Long nonOwnerId = 999L;
    String userRole = "OWNER";

    when(userServicePort.isOwnerOfRestaurant(nonOwnerId, restaurantId)).thenReturn(false);

    // Act & Assert
    assertThatThrownBy(
            () -> useCase.getRestaurantMetrics(restaurantId, nonOwnerId, userRole, null, null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("You are not authorized to view this restaurant's metrics");
  }

  @Test
  @DisplayName("Should throw exception when customer tries to access metrics")
  void shouldThrowExceptionWhenCustomerTriesToAccessMetrics() {
    // Arrange
    Long restaurantId = 456L;
    Long customerId = 789L;
    String userRole = "CUSTOMER";

    // Act & Assert
    assertThatThrownBy(
            () -> useCase.getRestaurantMetrics(restaurantId, customerId, userRole, null, null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("You are not authorized to view this restaurant's metrics");
  }

  @Test
  @DisplayName("Should allow admin to access any restaurant metrics")
  void shouldAllowAdminToAccessAnyRestaurantMetrics() {
    // Arrange
    Long restaurantId = 456L;
    Long adminId = 1L;
    String userRole = "ADMIN";

    when(userServicePort.getRestaurantName(restaurantId)).thenReturn("La Pizzeria");
    when(traceabilityPersistencePort.countOrdersByRestaurant(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(100);
    when(traceabilityPersistencePort.calculateAveragePreparationTime(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(30);
    when(traceabilityPersistencePort.calculateAverageDeliveryTime(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(45);
    when(traceabilityPersistencePort.calculateEmployeeMetrics(
            eq(restaurantId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Arrays.asList());

    // Act
    RestaurantMetrics result =
        useCase.getRestaurantMetrics(restaurantId, adminId, userRole, null, null);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRestaurantId()).isEqualTo(restaurantId);

    // Admin access doesn't need to verify restaurant ownership
    verify(userServicePort, never()).isOwnerOfRestaurant(anyLong(), anyLong());
  }

  @Test
  @DisplayName("Should throw exception when parameters are null")
  void shouldThrowExceptionWhenParametersAreNull() {
    // Act & Assert
    assertThatThrownBy(() -> useCase.getRestaurantMetrics(null, 123L, "OWNER", null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Restaurant ID cannot be null");

    assertThatThrownBy(() -> useCase.getRestaurantMetrics(456L, null, "OWNER", null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Requesting user ID cannot be null");

    assertThatThrownBy(() -> useCase.getRestaurantMetrics(456L, 123L, null, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User role cannot be null or empty");

    assertThatThrownBy(() -> useCase.getRestaurantMetrics(456L, 123L, "", null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User role cannot be null or empty");
  }
}
