package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.TraceabilityEventEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ITraceabilityEventRepository
    extends MongoRepository<TraceabilityEventEntity, String> {

  /**
   * Find all traceability events for a specific order, ordered by event date
   *
   * @param orderId The order ID
   * @return List of traceability events for the order
   */
  List<TraceabilityEventEntity> findByOrderIdOrderByEventDateAsc(Long orderId);

  /**
   * Find all traceability events for orders from a specific restaurant within a date range
   *
   * @param restaurantId The restaurant ID
   * @param startDate Start of the date range (inclusive)
   * @param endDate End of the date range (inclusive)
   * @return List of traceability events for the restaurant within the date range
   */
  @Query("{ 'restaurant_id': ?0, 'event_date': { $gte: ?1, $lte: ?2 } }")
  List<TraceabilityEventEntity> findByRestaurantIdAndEventDateBetween(
      Long restaurantId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Find all traceability events for a specific restaurant and order status Used for metrics
   * calculation - finding completed orders, status changes, etc.
   *
   * @param restaurantId The restaurant ID
   * @param newStatus The status to filter by
   * @param startDate Start of the date range (inclusive)
   * @param endDate End of the date range (inclusive)
   * @return List of traceability events matching the criteria
   */
  @Query("{ 'restaurant_id': ?0, 'new_status': ?1, 'event_date': { $gte: ?2, $lte: ?3 } }")
  List<TraceabilityEventEntity> findByRestaurantIdAndNewStatusAndEventDateBetween(
      Long restaurantId, String newStatus, LocalDateTime startDate, LocalDateTime endDate);
}
