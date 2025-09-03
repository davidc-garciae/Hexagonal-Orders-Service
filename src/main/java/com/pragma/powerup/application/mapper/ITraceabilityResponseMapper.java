package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.EmployeeMetricsResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantMetricsResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityEventResponseDto;
import com.pragma.powerup.application.util.TimeFormatUtil;
import com.pragma.powerup.domain.model.EmployeeMetrics;
import com.pragma.powerup.domain.model.RestaurantMetrics;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    imports = {TimeFormatUtil.class})
public interface ITraceabilityResponseMapper {

  TraceabilityEventResponseDto toResponseDto(TraceabilityEvent traceabilityEvent);

  List<TraceabilityEventResponseDto> toResponseDtoList(List<TraceabilityEvent> traceabilityEvents);

  @Mapping(target = "employeeRanking", source = "employeeRankings")
  @Mapping(target = "totalOrdersProcessed", source = "totalOrders")
  @Mapping(
      target = "averageOrderPreparationTime",
      expression =
          "java(TimeFormatUtil.formatSecondsToMinSec(restaurantMetrics.getAveragePreparationTime()))")
  @Mapping(
      target = "averageOrderDeliveryTime",
      expression =
          "java(TimeFormatUtil.formatSecondsToMinSec(restaurantMetrics.getAverageDeliveryTime()))")
  @Mapping(
      target = "activeEmployees",
      expression =
          "java(restaurantMetrics.getEmployeeRankings() != null ? restaurantMetrics.getEmployeeRankings().size() : 0)")
  RestaurantMetricsResponseDto toResponseDto(RestaurantMetrics restaurantMetrics);

  @Mapping(
      target = "averagePreparationTime",
      expression =
          "java(TimeFormatUtil.formatSecondsToMinSec(employeeMetrics.getAveragePreparationTime()))")
  @Mapping(
      target = "averageDeliveryTime",
      expression =
          "java(TimeFormatUtil.formatSecondsToMinSec(employeeMetrics.getAverageDeliveryTime()))")
  EmployeeMetricsResponseDto toResponseDto(EmployeeMetrics employeeMetrics);

  List<EmployeeMetricsResponseDto> toEmployeeResponseDtoList(List<EmployeeMetrics> employeeMetrics);
}
