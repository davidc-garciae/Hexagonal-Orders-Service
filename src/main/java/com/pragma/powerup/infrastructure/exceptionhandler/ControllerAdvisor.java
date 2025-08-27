package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.ActiveOrderExistsException;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

  private static final String MESSAGE = "message";

  @ExceptionHandler(NoDataFoundException.class)
  public ResponseEntity<Map<String, String>> handleNoDataFoundException(
      NoDataFoundException ignoredNoDataFoundException) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Collections.singletonMap(MESSAGE, ExceptionResponse.NO_DATA_FOUND.getMessage()));
  }

  @ExceptionHandler(ActiveOrderExistsException.class)
  public ResponseEntity<Map<String, String>> handleConflict(ActiveOrderExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Collections.singletonMap(MESSAGE, ex.getMessage()));
  }

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<Map<String, String>> handleDomain(DomainException ex) {
    return ResponseEntity.badRequest().body(Collections.singletonMap(MESSAGE, ex.getMessage()));
  }
}
