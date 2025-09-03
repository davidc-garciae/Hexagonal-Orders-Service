package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RestaurantMetrics;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.domain.spi.ITraceabilityPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import java.time.LocalDate;
import java.util.List;

public class TraceabilityUseCase implements ITraceabilityServicePort {

  private final ITraceabilityPersistencePort traceabilityPersistencePort;
  private final IUserServicePort userServicePort;

  public TraceabilityUseCase(
      ITraceabilityPersistencePort traceabilityPersistencePort, IUserServicePort userServicePort) {
    this.traceabilityPersistencePort = traceabilityPersistencePort;
    this.userServicePort = userServicePort;
  }

  @Override
  public void createTraceabilityEvent(TraceabilityEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }

    if (event.getOrderId() == null) {
      throw new IllegalArgumentException("Order ID cannot be null");
    }

    if (event.getCustomerId() == null) {
      throw new IllegalArgumentException("Customer ID cannot be null");
    }

    if (event.getRestaurantId() == null) {
      throw new IllegalArgumentException("Restaurant ID cannot be null");
    }

    if (event.getEventType() == null) {
      throw new IllegalArgumentException("Event type cannot be null");
    }

    if (event.getNewStatus() == null) {
      throw new IllegalArgumentException("New status cannot be null");
    }

    if (event.getTimestamp() == null) {
      throw new IllegalArgumentException("Timestamp cannot be null");
    }

    traceabilityPersistencePort.saveEvent(event);
  }

  @Override
  public List<TraceabilityEvent> getOrderTraceability(
      Long orderId, Long requestingUserId, String userRole) {

    validateTraceabilityRequest(orderId, requestingUserId, userRole);

    List<TraceabilityEvent> events = traceabilityPersistencePort.findEventsByOrderId(orderId);

    if (events.isEmpty()) {
      throw new DomainException("Order not found: " + orderId);
    }

    if (!isAuthorizedToViewOrder(events, requestingUserId, userRole)) {
      throw new DomainException("You are not authorized to view this order's traceability");
    }

    return events;
  }

  @Override
  public RestaurantMetrics getRestaurantMetrics(
      Long restaurantId,
      Long requestingUserId,
      String userRole,
      LocalDate startDate,
      LocalDate endDate) {

    validateMetricsRequest(restaurantId, requestingUserId, userRole);

    if (!isAuthorizedToViewMetrics(restaurantId, requestingUserId, userRole)) {
      throw new DomainException("You are not authorized to view this restaurant's metrics");
    }

    LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusMonths(1);
    LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

    return buildRestaurantMetrics(restaurantId, effectiveStartDate, effectiveEndDate);
  }

  private void validateTraceabilityRequest(Long orderId, Long requestingUserId, String userRole) {
    if (orderId == null) {
      throw new IllegalArgumentException("Order ID cannot be null");
    }

    if (requestingUserId == null) {
      throw new IllegalArgumentException("Requesting user ID cannot be null");
    }

    if (userRole == null || userRole.trim().isEmpty()) {
      throw new IllegalArgumentException("User role cannot be null or empty");
    }
  }

  private void validateMetricsRequest(Long restaurantId, Long requestingUserId, String userRole) {
    if (restaurantId == null) {
      throw new IllegalArgumentException("Restaurant ID cannot be null");
    }

    if (requestingUserId == null) {
      throw new IllegalArgumentException("Requesting user ID cannot be null");
    }

    if (userRole == null || userRole.trim().isEmpty()) {
      throw new IllegalArgumentException("User role cannot be null or empty");
    }
  }

  private boolean isAuthorizedToViewOrder(
      List<TraceabilityEvent> events, Long requestingUserId, String userRole) {

    if ("ADMIN".equals(userRole)) {
      return true;
    }

    if ("CUSTOMER".equals(userRole)) {
      // Check if the first event (which should be the order creation) belongs to this
      // customer
      // All events for an order should belong to the same customer
      if (!events.isEmpty()) {
        TraceabilityEvent firstEvent = events.get(0);
        return firstEvent.belongsToCustomer(requestingUserId);
      }
      return false;
    }

    // For OWNER and EMPLOYEE, check restaurant authorization
    // Find the first event that has a restaurantId set
    TraceabilityEvent eventWithRestaurant =
        events.stream().filter(event -> event.getRestaurantId() != null).findFirst().orElse(null);

    if (eventWithRestaurant == null) {
      return false; // No restaurant info available for authorization
    }

    if ("OWNER".equals(userRole)) {
      return userServicePort.isOwnerOfRestaurant(
          requestingUserId, eventWithRestaurant.getRestaurantId());
    }

    if ("EMPLOYEE".equals(userRole)) {
      return userServicePort.isEmployeeOfRestaurant(
          requestingUserId, eventWithRestaurant.getRestaurantId());
    }

    return false;
  }

  private boolean isAuthorizedToViewMetrics(
      Long restaurantId, Long requestingUserId, String userRole) {

    // Allow ADMIN to view any restaurant's metrics
    if ("ADMIN".equals(userRole)) {
      return true;
    }

    // HU-018: Only restaurant owner can view efficiency metrics
    if ("OWNER".equals(userRole)) {
      return userServicePort.isOwnerOfRestaurant(requestingUserId, restaurantId);
    }

    // No other roles allowed
    return false;
  }

  private RestaurantMetrics buildRestaurantMetrics(
      Long restaurantId, LocalDate startDate, LocalDate endDate) {

    return RestaurantMetrics.builder()
        .restaurantId(restaurantId)
        .restaurantName(userServicePort.getRestaurantName(restaurantId))
        .totalOrders(
            traceabilityPersistencePort.countOrdersByRestaurant(restaurantId, startDate, endDate))
        .averagePreparationTime(
            traceabilityPersistencePort.calculateAveragePreparationTime(
                restaurantId, startDate, endDate))
        .averageDeliveryTime(
            traceabilityPersistencePort.calculateAverageDeliveryTime(
                restaurantId, startDate, endDate))
        .employeeRankings(
            traceabilityPersistencePort.calculateEmployeeMetrics(restaurantId, startDate, endDate))
        .periodStart(startDate)
        .periodEnd(endDate)
        .build();
  }
}
