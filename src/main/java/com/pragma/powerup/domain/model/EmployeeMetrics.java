package com.pragma.powerup.domain.model;

public class EmployeeMetrics {
  private Long employeeId;
  private String employeeName;
  private Integer ordersProcessed;
  private Integer averagePreparationTime;
  private Integer averageDeliveryTime;
  private Integer efficiencyRank;

  public EmployeeMetrics() {
    // Default constructor for JPA and mappers
  }

  public static EmployeeMetricsBuilder builder() {
    return new EmployeeMetricsBuilder();
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

  public Integer getEfficiencyRank() {
    return efficiencyRank;
  }

  public void setEfficiencyRank(Integer efficiencyRank) {
    this.efficiencyRank = efficiencyRank;
  }

  public static class EmployeeMetricsBuilder {
    private Long employeeId;
    private String employeeName;
    private Integer ordersProcessed;
    private Integer averagePreparationTime;
    private Integer averageDeliveryTime;
    private Integer efficiencyRank;

    public EmployeeMetricsBuilder employeeId(Long employeeId) {
      this.employeeId = employeeId;
      return this;
    }

    public EmployeeMetricsBuilder employeeName(String employeeName) {
      this.employeeName = employeeName;
      return this;
    }

    public EmployeeMetricsBuilder ordersProcessed(Integer ordersProcessed) {
      this.ordersProcessed = ordersProcessed;
      return this;
    }

    public EmployeeMetricsBuilder averagePreparationTime(Integer averagePreparationTime) {
      this.averagePreparationTime = averagePreparationTime;
      return this;
    }

    public EmployeeMetricsBuilder averageDeliveryTime(Integer averageDeliveryTime) {
      this.averageDeliveryTime = averageDeliveryTime;
      return this;
    }

    public EmployeeMetricsBuilder efficiencyRank(Integer efficiencyRank) {
      this.efficiencyRank = efficiencyRank;
      return this;
    }

    public EmployeeMetrics build() {
      EmployeeMetrics metrics = new EmployeeMetrics();
      metrics.employeeId = this.employeeId;
      metrics.employeeName = this.employeeName;
      metrics.ordersProcessed = this.ordersProcessed;
      metrics.averagePreparationTime = this.averagePreparationTime;
      metrics.averageDeliveryTime = this.averageDeliveryTime;
      metrics.efficiencyRank = this.efficiencyRank;
      return metrics;
    }
  }
}
