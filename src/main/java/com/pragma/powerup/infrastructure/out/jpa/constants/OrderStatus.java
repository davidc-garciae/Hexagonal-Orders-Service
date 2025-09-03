package com.pragma.powerup.infrastructure.out.jpa.constants;

public final class OrderStatus {

  public static final String PENDING = "PENDING";
  public static final String IN_PREPARATION = "IN_PREPARATION";
  public static final String READY = "READY";
  public static final String DELIVERED = "DELIVERED";
  public static final String CANCELLED = "CANCELLED";

  private OrderStatus() {
    // Utility class
  }
}
