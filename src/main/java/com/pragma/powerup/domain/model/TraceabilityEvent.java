package com.pragma.powerup.domain.model;

import java.time.LocalDateTime;

public class TraceabilityEvent {
  private String id;
  private Long orderId;
  private Long customerId;
  private Long restaurantId;
  private String eventType;
  private String previousStatus;
  private String newStatus;
  private LocalDateTime timestamp;
  private Long employeeId;
  private String employeeName;

  public TraceabilityEvent() {
    // Default constructor for JPA and mappers
  }

  public boolean belongsToCustomer(Long customerId) {
    return this.customerId != null && this.customerId.equals(customerId);
  }

  public boolean belongsToRestaurant(Long restaurantId) {
    return this.restaurantId != null && this.restaurantId.equals(restaurantId);
  }

  public static TraceabilityEventBuilder builder() {
    return new TraceabilityEventBuilder();
  }

  public static class TraceabilityEventBuilder {
    private String id;
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String eventType;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp;
    private Long employeeId;
    private String employeeName;

    public TraceabilityEventBuilder id(String id) {
      this.id = id;
      return this;
    }

    public TraceabilityEventBuilder orderId(Long orderId) {
      this.orderId = orderId;
      return this;
    }

    public TraceabilityEventBuilder customerId(Long customerId) {
      this.customerId = customerId;
      return this;
    }

    public TraceabilityEventBuilder restaurantId(Long restaurantId) {
      this.restaurantId = restaurantId;
      return this;
    }

    public TraceabilityEventBuilder eventType(String eventType) {
      this.eventType = eventType;
      return this;
    }

    public TraceabilityEventBuilder previousStatus(String previousStatus) {
      this.previousStatus = previousStatus;
      return this;
    }

    public TraceabilityEventBuilder newStatus(String newStatus) {
      this.newStatus = newStatus;
      return this;
    }

    public TraceabilityEventBuilder timestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public TraceabilityEventBuilder employeeId(Long employeeId) {
      this.employeeId = employeeId;
      return this;
    }

    public TraceabilityEventBuilder employeeName(String employeeName) {
      this.employeeName = employeeName;
      return this;
    }

    public TraceabilityEvent build() {
      TraceabilityEvent event = new TraceabilityEvent();
      event.id = this.id;
      event.orderId = this.orderId;
      event.customerId = this.customerId;
      event.restaurantId = this.restaurantId;
      event.eventType = this.eventType;
      event.previousStatus = this.previousStatus;
      event.newStatus = this.newStatus;
      event.timestamp = this.timestamp;
      event.employeeId = this.employeeId;
      event.employeeName = this.employeeName;
      return event;
    }
  }

  // Getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(Long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getPreviousStatus() {
    return previousStatus;
  }

  public void setPreviousStatus(String previousStatus) {
    this.previousStatus = previousStatus;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(String newStatus) {
    this.newStatus = newStatus;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Long getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Long employeeId) {
    this.employeeId = employeeId;
  }

  public String getEmployeeName() {
    return employeeName;
  }

  public void setEmployeeName(String employeeName) {
    this.employeeName = employeeName;
  }
}
