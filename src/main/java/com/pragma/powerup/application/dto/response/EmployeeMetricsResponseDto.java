package com.pragma.powerup.application.dto.response;

public class EmployeeMetricsResponseDto {
  private Long employeeId;
  private String employeeName;
  private Integer ordersProcessed;

  /** Average preparation time per order in MM:SS format */
  private String averagePreparationTime;

  /** Average delivery time per order in MM:SS format */
  private String averageDeliveryTime;

  /** Efficiency ranking (1 = most efficient, based on total time) */
  private Integer efficiencyRank;

  public EmployeeMetricsResponseDto() {
    // Default constructor for JSON serialization
  }

  // Getters and setters
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

  public Integer getOrdersProcessed() {
    return ordersProcessed;
  }

  public void setOrdersProcessed(Integer ordersProcessed) {
    this.ordersProcessed = ordersProcessed;
  }

  public String getAveragePreparationTime() {
    return averagePreparationTime;
  }

  public void setAveragePreparationTime(String averagePreparationTime) {
    this.averagePreparationTime = averagePreparationTime;
  }

  public String getAverageDeliveryTime() {
    return averageDeliveryTime;
  }

  public void setAverageDeliveryTime(String averageDeliveryTime) {
    this.averageDeliveryTime = averageDeliveryTime;
  }

  public Integer getEfficiencyRank() {
    return efficiencyRank;
  }

  public void setEfficiencyRank(Integer efficiencyRank) {
    this.efficiencyRank = efficiencyRank;
  }
}
