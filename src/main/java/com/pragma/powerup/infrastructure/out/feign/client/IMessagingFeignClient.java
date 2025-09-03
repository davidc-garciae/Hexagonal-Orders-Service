package com.pragma.powerup.infrastructure.out.feign.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "messaging-service", url = "${feign.clients.messaging-service.url}")
public interface IMessagingFeignClient {

  @PostMapping("/api/v1/sms")
  void sendSms(@RequestBody Map<String, String> smsData);
}
