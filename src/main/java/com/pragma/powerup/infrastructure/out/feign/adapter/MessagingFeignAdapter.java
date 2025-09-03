package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.spi.IMessagingFeignPort;
import com.pragma.powerup.infrastructure.out.feign.client.IMessagingFeignClient;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagingFeignAdapter implements IMessagingFeignPort {

  private final IMessagingFeignClient messagingFeignClient;

  @Override
  public void sendSms(String phone, String message) {
    Map<String, String> smsData = new HashMap<>();
    smsData.put("phone", phone);
    smsData.put("message", message);
    messagingFeignClient.sendSms(smsData);
  }
}
