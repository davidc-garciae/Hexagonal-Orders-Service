package com.pragma.powerup.domain.model;

import java.time.LocalDate;
import java.util.List;

public class RestaurantMetrics {
  private Long restaurantId;
  private String restaurantName;
  private Integer totalOrders;
  private Integer averagePreparationTime;
  private Integer averageDeliveryTime;
  private LocalDate periodStart;
  private LocalDate periodEnd;
  private List<EmployeeMetrics> employeeRankings;

  public RestaurantMetrics() {
    // Default constructor for JPA and mappers
  }

  public static RestaurantMetricsBuilder builder() {
    return new RestaurantMetricsBuilder();
  }

  public void sortEmployeesByEfficiency() {
    if (employeeRankings == null) {
      return;
    }

    employeeRankings.sort(this::compareEmployeeEfficiency);
    assignRankings();
  }

  private int compareEmployeeEfficiency(EmployeeMetrics e1, EmployeeMetrics e2) {
    int time1 =
        e1.getAveragePreparationTime() != null ? e1.getAveragePreparationTime() : Integer.MAX_VALUE;
    int time2 =
        e2.getAveragePreparationTime() != null ? e2.getAveragePreparationTime() : Integer.MAX_VALUE;

    int timeComparison = Integer.compare(time1, time2);
    if (timeComparison != 0) {
      return timeComparison;
    }

    int orders1 = e1.getOrdersProcessed() != null ? e1.getOrdersProcessed() : 0;
    int orders2 = e2.getOrdersProcessed() != null ? e2.getOrdersProcessed() : 0;

    return Integer.compare(orders2, orders1); // descending for orders
  }

  private void assignRankings() {
    for (int i = 0; i < employeeRankings.size(); i++) {
      employeeRankings.get(i).setEfficiencyRank(i + 1);
    }
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

  public Integer getTotalOrders() {
    return totalOrders;
  }

  public void setTotalOrders(Integer totalOrders) {
    this.totalOrders = totalOrders;
  }

  public Integer getAveragePreparationTime() {
    return averagePreparationTime;
  }

  public void setAveragePreparationTime(Integer averagePreparationTime) {
    this.averagePreparationTime = averagePreparationTime;
  }

  public Integer getAverageDeliveryTime() {
    return averageDeliveryTime;
  }

  public void setAverageDeliveryTime(Integer averageDeliveryTime) {
    this.averageDeliveryTime = averageDeliveryTime;
  }

  public LocalDate getPeriodStart() {
    return periodStart;
  }

  public void setPeriodStart(LocalDate periodStart) {
    this.periodStart = periodStart;
  }

  public LocalDate getPeriodEnd() {
    return periodEnd;
  }

  public void setPeriodEnd(LocalDate periodEnd) {
    this.periodEnd = periodEnd;
  }

  public List<EmployeeMetrics> getEmployeeRankings() {
    return employeeRankings;
  }

  public void setEmployeeRankings(List<EmployeeMetrics> employeeRankings) {
    this.employeeRankings = employeeRankings;
  }

  public static class RestaurantMetricsBuilder {
    private Long restaurantId;
    private String restaurantName;
    private Integer totalOrders;
    private Integer averagePreparationTime;
    private Integer averageDeliveryTime;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private List<EmployeeMetrics> employeeRankings;

    public RestaurantMetricsBuilder restaurantId(Long restaurantId) {
      this.restaurantId = restaurantId;
      return this;
    }

    public RestaurantMetricsBuilder restaurantName(String restaurantName) {
      this.restaurantName = restaurantName;
      return this;
    }

    public RestaurantMetricsBuilder totalOrders(Integer totalOrders) {
      this.totalOrders = totalOrders;
      return this;
    }

    public RestaurantMetricsBuilder averagePreparationTime(Integer averagePreparationTime) {
      this.averagePreparationTime = averagePreparationTime;
      return this;
    }

    public RestaurantMetricsBuilder averageDeliveryTime(Integer averageDeliveryTime) {
      this.averageDeliveryTime = averageDeliveryTime;
      return this;
    }

    public RestaurantMetricsBuilder periodStart(LocalDate periodStart) {
      this.periodStart = periodStart;
      return this;
    }

    public RestaurantMetricsBuilder periodEnd(LocalDate periodEnd) {
      this.periodEnd = periodEnd;
      return this;
    }

    public RestaurantMetricsBuilder employeeRankings(List<EmployeeMetrics> employeeRankings) {
      this.employeeRankings = employeeRankings;
      return this;
    }

    public RestaurantMetrics build() {
      RestaurantMetrics metrics = new RestaurantMetrics();
      metrics.restaurantId = this.restaurantId;
      metrics.restaurantName = this.restaurantName;
      metrics.totalOrders = this.totalOrders;
      metrics.averagePreparationTime = this.averagePreparationTime;
      metrics.averageDeliveryTime = this.averageDeliveryTime;
      metrics.periodStart = this.periodStart;
      metrics.periodEnd = this.periodEnd;
      metrics.employeeRankings = this.employeeRankings;
      return metrics;
    }
  }
}
