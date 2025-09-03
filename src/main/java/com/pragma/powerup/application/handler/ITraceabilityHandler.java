package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.RestaurantMetricsResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityEventResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface ITraceabilityHandler {

  /**
   * Get traceability events for a specific order
   *
   * @param orderId The ID of the order
   * @param jwtToken The JWT token for authorization
   * @return List of traceability events for the order
   */
  List<TraceabilityEventResponseDto> getOrderTraceability(Long orderId, String jwtToken);

  /**
   * Get efficiency metrics for a restaurant
   *
   * @param restaurantId The ID of the restaurant
   * @param periodStart Start date for metrics calculation (optional)
   * @param periodEnd End date for metrics calculation (optional)
   * @param jwtToken The JWT token for authorization
   * @return Restaurant metrics with employee rankings
   */
  RestaurantMetricsResponseDto getRestaurantMetrics(
      Long restaurantId, LocalDate periodStart, LocalDate periodEnd, String jwtToken);
}
