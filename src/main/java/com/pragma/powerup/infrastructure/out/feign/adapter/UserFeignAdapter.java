package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserFeignPort;
import com.pragma.powerup.infrastructure.out.feign.client.IUsersFeignClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.IUserFeignMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFeignAdapter implements IUserFeignPort {

  private final IUsersFeignClient usersFeignClient;
  private final IUserFeignMapper userFeignMapper;

  @Override
  public UserModel getUserById(Long id) {
    return userFeignMapper.toUserModel(usersFeignClient.getUserById(id));
  }
}
