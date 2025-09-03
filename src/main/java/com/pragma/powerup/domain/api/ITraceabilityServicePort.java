package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantMetrics;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import java.time.LocalDate;
import java.util.List;

public interface ITraceabilityServicePort {

  void createTraceabilityEvent(TraceabilityEvent event);

  List<TraceabilityEvent> getOrderTraceability(
      Long orderId, Long requestingUserId, String userRole);

  RestaurantMetrics getRestaurantMetrics(
      Long restaurantId,
      Long requestingUserId,
      String userRole,
      LocalDate startDate,
      LocalDate endDate);
}
