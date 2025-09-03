package com.pragma.powerup.domain.spi;

public interface IMessagingFeignPort {
  void sendSms(String phone, String message);
}
