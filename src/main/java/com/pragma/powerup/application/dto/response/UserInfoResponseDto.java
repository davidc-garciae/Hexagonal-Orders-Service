package com.pragma.powerup.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String role;
}
