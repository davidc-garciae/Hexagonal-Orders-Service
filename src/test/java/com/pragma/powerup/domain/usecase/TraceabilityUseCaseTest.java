package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.EmployeeMetrics;
import com.pragma.powerup.domain.model.RestaurantMetrics;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pragma.powerup.testdata.TestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraceabilityUseCase Domain Tests")
class TraceabilityUseCaseTest {

    @Mock
    private ITraceabilityPersistencePort traceabilityPersistencePort;

    @Mock
    private IUserServicePort userServicePort;

    private TraceabilityUseCase traceabilityUseCase;

    @BeforeEach
    void setUp() {
        traceabilityUseCase = new TraceabilityUseCase(traceabilityPersistencePort, userServicePort);
    }

    @Test
    @DisplayName("Should create traceability event successfully")
    void createTraceabilityEvent_WithValidEvent_ShouldSaveEvent() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();

        // When
        traceabilityUseCase.createTraceabilityEvent(event);

        // Then
        verify(traceabilityPersistencePort).saveEvent(event);
    }

    @Test
    @DisplayName("Should throw exception when event is null")
    void createTraceabilityEvent_WithNullEvent_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should throw exception when order ID is null")
    void createTraceabilityEvent_WithNullOrderId_ShouldThrowException() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();
        event.setOrderId(null);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order ID cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should throw exception when customer ID is null")
    void createTraceabilityEvent_WithNullCustomerId_ShouldThrowException() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();
        event.setCustomerId(null);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer ID cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should throw exception when restaurant ID is null")
    void createTraceabilityEvent_WithNullRestaurantId_ShouldThrowException() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();
        event.setRestaurantId(null);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant ID cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should throw exception when event type is null")
    void createTraceabilityEvent_WithNullEventType_ShouldThrowException() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();
        event.setEventType(null);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event type cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should throw exception when new status is null")
    void createTraceabilityEvent_WithNullNewStatus_ShouldThrowException() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();
        event.setNewStatus(null);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New status cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should throw exception when timestamp is null")
    void createTraceabilityEvent_WithNullTimestamp_ShouldThrowException() {
        // Given
        TraceabilityEvent event = validTraceabilityEvent();
        event.setTimestamp(null);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.createTraceabilityEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Timestamp cannot be null");

        verifyNoInteractions(traceabilityPersistencePort);
    }

    @Test
    @DisplayName("Should get order traceability for customer successfully")
    void getOrderTraceability_ForCustomer_ShouldReturnEvents() {
        // Given
        List<TraceabilityEvent> events = Arrays.asList(
                traceabilityEventForCustomer(CUSTOMER_ID),
                traceabilityEventForCustomer(CUSTOMER_ID));

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);

        // When
        List<TraceabilityEvent> result = traceabilityUseCase.getOrderTraceability(
                ORDER_ID, CUSTOMER_ID, "CUSTOMER");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(events);

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
    }

    @Test
    @DisplayName("Should get order traceability for admin successfully")
    void getOrderTraceability_ForAdmin_ShouldReturnEvents() {
        // Given
        List<TraceabilityEvent> events = Collections.singletonList(validTraceabilityEvent());

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);

        // When
        List<TraceabilityEvent> result = traceabilityUseCase.getOrderTraceability(
                ORDER_ID, ADMIN_ID, "ADMIN");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
    }

    @Test
    @DisplayName("Should get order traceability for restaurant owner successfully")
    void getOrderTraceability_ForOwner_ShouldReturnEvents() {
        // Given
        List<TraceabilityEvent> events = Collections.singletonList(validTraceabilityEvent());

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);
        when(userServicePort.isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID)).thenReturn(true);

        // When
        List<TraceabilityEvent> result = traceabilityUseCase.getOrderTraceability(
                ORDER_ID, OWNER_ID, "OWNER");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
        verify(userServicePort).isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should get order traceability for restaurant employee successfully")
    void getOrderTraceability_ForEmployee_ShouldReturnEvents() {
        // Given
        List<TraceabilityEvent> events = Collections.singletonList(validTraceabilityEvent());

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);
        when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(true);

        // When
        List<TraceabilityEvent> result = traceabilityUseCase.getOrderTraceability(
                ORDER_ID, EMPLOYEE_ID, "EMPLOYEE");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
        verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void getOrderTraceability_WithNonExistentOrder_ShouldThrowException() {
        // Given
        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, CUSTOMER_ID, "CUSTOMER"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Order not found");

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
    }

    @Test
    @DisplayName("Should throw exception when customer not authorized")
    void getOrderTraceability_WithUnauthorizedCustomer_ShouldThrowException() {
        // Given
        List<TraceabilityEvent> events = Collections.singletonList(traceabilityEventForCustomer(999L));

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, CUSTOMER_ID, "CUSTOMER"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not authorized to view this order's traceability");

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
    }

    @Test
    @DisplayName("Should throw exception when owner not authorized")
    void getOrderTraceability_WithUnauthorizedOwner_ShouldThrowException() {
        // Given
        List<TraceabilityEvent> events = Collections.singletonList(validTraceabilityEvent());

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);
        when(userServicePort.isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, OWNER_ID, "OWNER"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not authorized to view this order's traceability");

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
        verify(userServicePort).isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should throw exception when employee not authorized")
    void getOrderTraceability_WithUnauthorizedEmployee_ShouldThrowException() {
        // Given
        List<TraceabilityEvent> events = Collections.singletonList(validTraceabilityEvent());

        when(traceabilityPersistencePort.findEventsByOrderId(ORDER_ID)).thenReturn(events);
        when(userServicePort.isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, EMPLOYEE_ID, "EMPLOYEE"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not authorized to view this order's traceability");

        verify(traceabilityPersistencePort).findEventsByOrderId(ORDER_ID);
        verify(userServicePort).isEmployeeOfRestaurant(EMPLOYEE_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should get restaurant metrics for owner successfully")
    void getRestaurantMetrics_ForOwner_ShouldReturnMetrics() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        EmployeeMetrics employeeMetrics = validEmployeeMetrics();

        when(userServicePort.isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID)).thenReturn(true);
        when(userServicePort.getRestaurantName(RESTAURANT_ID)).thenReturn(RESTAURANT_NAME);
        when(traceabilityPersistencePort.countOrdersByRestaurant(RESTAURANT_ID, startDate, endDate)).thenReturn(50);
        when(traceabilityPersistencePort.calculateAveragePreparationTime(RESTAURANT_ID, startDate, endDate))
                .thenReturn(25);
        when(traceabilityPersistencePort.calculateAverageDeliveryTime(RESTAURANT_ID, startDate, endDate))
                .thenReturn(15);
        when(traceabilityPersistencePort.calculateEmployeeMetrics(RESTAURANT_ID, startDate, endDate))
                .thenReturn(Collections.singletonList(employeeMetrics));

        // When
        RestaurantMetrics result = traceabilityUseCase.getRestaurantMetrics(
                RESTAURANT_ID, OWNER_ID, "OWNER", startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRestaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(result.getRestaurantName()).isEqualTo(RESTAURANT_NAME);
        assertThat(result.getTotalOrders()).isEqualTo(50);
        assertThat(result.getAveragePreparationTime()).isEqualTo(25);
        assertThat(result.getAverageDeliveryTime()).isEqualTo(15);
        assertThat(result.getEmployeeRankings()).hasSize(1);
        assertThat(result.getPeriodStart()).isEqualTo(startDate);
        assertThat(result.getPeriodEnd()).isEqualTo(endDate);

        verify(userServicePort).isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should throw exception when non-owner tries to get metrics")
    void getRestaurantMetrics_ForNonOwner_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getRestaurantMetrics(
                RESTAURANT_ID, CUSTOMER_ID, "CUSTOMER", null, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not authorized to view this restaurant's metrics");
    }

    @Test
    @DisplayName("Should use default dates when not provided")
    void getRestaurantMetrics_WithNullDates_ShouldUseDefaults() {
        // Given
        when(userServicePort.isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID)).thenReturn(true);
        when(userServicePort.getRestaurantName(RESTAURANT_ID)).thenReturn(RESTAURANT_NAME);
        when(traceabilityPersistencePort.countOrdersByRestaurant(eq(RESTAURANT_ID), any(LocalDate.class),
                any(LocalDate.class))).thenReturn(10);
        when(traceabilityPersistencePort.calculateAveragePreparationTime(eq(RESTAURANT_ID), any(LocalDate.class),
                any(LocalDate.class))).thenReturn(20);
        when(traceabilityPersistencePort.calculateAverageDeliveryTime(eq(RESTAURANT_ID), any(LocalDate.class),
                any(LocalDate.class))).thenReturn(10);
        when(traceabilityPersistencePort.calculateEmployeeMetrics(eq(RESTAURANT_ID), any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // When
        RestaurantMetrics result = traceabilityUseCase.getRestaurantMetrics(
                RESTAURANT_ID, OWNER_ID, "OWNER", null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPeriodStart()).isBefore(result.getPeriodEnd());

        verify(userServicePort).isOwnerOfRestaurant(OWNER_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should validate traceability request parameters")
    void getOrderTraceability_WithInvalidParameters_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(null, CUSTOMER_ID, "CUSTOMER"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order ID cannot be null");

        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, null, "CUSTOMER"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Requesting user ID cannot be null");

        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, CUSTOMER_ID, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User role cannot be null or empty");

        assertThatThrownBy(() -> traceabilityUseCase.getOrderTraceability(ORDER_ID, CUSTOMER_ID, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User role cannot be null or empty");
    }

    @Test
    @DisplayName("Should validate metrics request parameters")
    void getRestaurantMetrics_WithInvalidParameters_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> traceabilityUseCase.getRestaurantMetrics(null, OWNER_ID, "OWNER", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant ID cannot be null");

        assertThatThrownBy(() -> traceabilityUseCase.getRestaurantMetrics(RESTAURANT_ID, null, "OWNER", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Requesting user ID cannot be null");

        assertThatThrownBy(() -> traceabilityUseCase.getRestaurantMetrics(RESTAURANT_ID, OWNER_ID, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User role cannot be null or empty");

        assertThatThrownBy(() -> traceabilityUseCase.getRestaurantMetrics(RESTAURANT_ID, OWNER_ID, "", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User role cannot be null or empty");
    }
}
