package com.pragma.powerup.application.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPageResponseDto {
  private List<OrderResponseDto> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
}
