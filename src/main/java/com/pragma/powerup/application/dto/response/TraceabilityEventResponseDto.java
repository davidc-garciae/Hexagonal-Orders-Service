package com.pragma.powerup.application.dto.response;

import java.time.LocalDateTime;

public class TraceabilityEventResponseDto {
  private String id;
  private Long orderId;
  private String eventType;
  private String previousStatus;
  private String newStatus;
  private LocalDateTime timestamp;
  private Long employeeId;
  private String employeeName;

  public TraceabilityEventResponseDto() {
    // Default constructor for JSON serialization
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
