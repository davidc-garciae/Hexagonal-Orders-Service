package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.response.RestaurantMetricsResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityEventResponseDto;
import com.pragma.powerup.application.handler.ITraceabilityHandler;
import com.pragma.powerup.application.mapper.ITraceabilityResponseMapper;
import com.pragma.powerup.application.util.JwtSecurityUtils;
import com.pragma.powerup.domain.api.ITraceabilityServicePort;
import com.pragma.powerup.domain.model.RestaurantMetrics;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TraceabilityHandler implements ITraceabilityHandler {

  private final ITraceabilityServicePort traceabilityServicePort;
  private final ITraceabilityResponseMapper traceabilityResponseMapper;
  private final JwtSecurityUtils jwtSecurityUtils;

  @Override
  @Transactional(readOnly = true)
  public List<TraceabilityEventResponseDto> getOrderTraceability(Long orderId, String jwtToken) {
    Long userId = jwtSecurityUtils.getCurrentUserId();
    String userRole = jwtSecurityUtils.getCurrentUserRole();

    List<TraceabilityEvent> events =
        traceabilityServicePort.getOrderTraceability(orderId, userId, userRole);
    return traceabilityResponseMapper.toResponseDtoList(events);
  }

  @Override
  @Transactional(readOnly = true)
  public RestaurantMetricsResponseDto getRestaurantMetrics(
      Long restaurantId, LocalDate periodStart, LocalDate periodEnd, String jwtToken) {
    Long userId = jwtSecurityUtils.getCurrentUserId();
    String userRole = jwtSecurityUtils.getCurrentUserRole();

    RestaurantMetrics metrics =
        traceabilityServicePort.getRestaurantMetrics(
            restaurantId, userId, userRole, periodStart, periodEnd);
    return traceabilityResponseMapper.toResponseDto(metrics);
  }
}
