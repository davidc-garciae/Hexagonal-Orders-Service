package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.EmployeeMetrics;
import com.pragma.powerup.domain.model.TraceabilityEvent;
import java.time.LocalDate;
import java.util.List;

public interface ITraceabilityPersistencePort {

  void saveEvent(TraceabilityEvent event);

  List<TraceabilityEvent> findEventsByOrderId(Long orderId);

  Integer countOrdersByRestaurant(Long restaurantId, LocalDate startDate, LocalDate endDate);

  Integer calculateAveragePreparationTime(
      Long restaurantId, LocalDate startDate, LocalDate endDate);

  Integer calculateAverageDeliveryTime(Long restaurantId, LocalDate startDate, LocalDate endDate);

  List<EmployeeMetrics> calculateEmployeeMetrics(
      Long restaurantId, LocalDate startDate, LocalDate endDate);
}
