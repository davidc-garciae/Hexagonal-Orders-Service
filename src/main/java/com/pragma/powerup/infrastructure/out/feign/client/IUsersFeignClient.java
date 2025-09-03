package com.pragma.powerup.infrastructure.out.feign.client;

import com.pragma.powerup.application.dto.response.UserInfoResponseDto;
import com.pragma.powerup.infrastructure.out.feign.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "users-service",
    url = "${feign.clients.users-service.url}",
    configuration = FeignClientConfiguration.class)
public interface IUsersFeignClient {
  @GetMapping("/api/v1/users/{id}")
  UserInfoResponseDto getUserById(@PathVariable("id") Long id);
}
