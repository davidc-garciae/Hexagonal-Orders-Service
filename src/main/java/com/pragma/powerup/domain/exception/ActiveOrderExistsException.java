package com.pragma.powerup.domain.exception;

public class ActiveOrderExistsException extends DomainException {
  public ActiveOrderExistsException(String message) {
    super(message);
  }
}
