package com.pragma.powerup.infrastructure.out.jpa.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "traceability_events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TraceabilityEventEntity {

  @Id private String id;

  @Field("order_id")
  private Long orderId;

  @Field("customer_id")
  private Long customerId;

  @Field("employee_id")
  private Long employeeId;

  @Field("employee_name")
  private String employeeName;

  @Field("previous_status")
  private String previousStatus;

  @Field("new_status")
  private String newStatus;

  @Field("client_email")
  private String clientEmail;

  @Field("event_date")
  private LocalDateTime eventDate;

  @Field("additional_notes")
  private String additionalNotes;

  @Field("estimated_delivery_time")
  private LocalDateTime estimatedDeliveryTime;

  @Field("restaurant_id")
  private Long restaurantId;
}
