package com.pragma.powerup.application.dto.response;

import java.util.List;

public class RestaurantMetricsResponseDto {
  private Long restaurantId;
  private String restaurantName;
  private Integer totalOrdersProcessed;

  /** Average order preparation time in MM:SS format */
  private String averageOrderPreparationTime;

  /** Average order delivery time in MM:SS format */
  private String averageOrderDeliveryTime;

  private Integer activeEmployees;
  private List<EmployeeMetricsResponseDto> employeeRanking;

  public RestaurantMetricsResponseDto() {
    // Default constructor for JSON serialization
  }

  // Getters and setters
  public Long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(Long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public void setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
  }

  public Integer getTotalOrdersProcessed() {
    return totalOrdersProcessed;
  }

  public void setTotalOrdersProcessed(Integer totalOrdersProcessed) {
    this.totalOrdersProcessed = totalOrdersProcessed;
  }

  public String getAverageOrderPreparationTime() {
    return averageOrderPreparationTime;
  }

  public void setAverageOrderPreparationTime(String averageOrderPreparationTime) {
    this.averageOrderPreparationTime = averageOrderPreparationTime;
  }

  public String getAverageOrderDeliveryTime() {
    return averageOrderDeliveryTime;
  }

  public void setAverageOrderDeliveryTime(String averageOrderDeliveryTime) {
    this.averageOrderDeliveryTime = averageOrderDeliveryTime;
  }

  public Integer getActiveEmployees() {
    return activeEmployees;
  }

  public void setActiveEmployees(Integer activeEmployees) {
    this.activeEmployees = activeEmployees;
  }

  public List<EmployeeMetricsResponseDto> getEmployeeRanking() {
    return employeeRanking;
  }

  public void setEmployeeRanking(List<EmployeeMetricsResponseDto> employeeRanking) {
    this.employeeRanking = employeeRanking;
  }
}
