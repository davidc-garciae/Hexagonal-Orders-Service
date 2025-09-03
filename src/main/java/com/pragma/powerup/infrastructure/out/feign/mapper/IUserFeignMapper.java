package com.pragma.powerup.infrastructure.out.feign.mapper;

import com.pragma.powerup.application.dto.response.UserInfoResponseDto;
import com.pragma.powerup.domain.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserFeignMapper {

  // Método personalizado para mapear UserInfoResponseDto a UserModel
  default UserModel toUserModel(UserInfoResponseDto userInfoResponseDto) {
    if (userInfoResponseDto == null) {
      return null;
    }

    return UserModel.builder()
        .id(userInfoResponseDto.getId())
        .name(userInfoResponseDto.getFirstName())
        .lastname(userInfoResponseDto.getLastName())
        .email(userInfoResponseDto.getEmail())
        .phone(userInfoResponseDto.getPhone())
        .role(userInfoResponseDto.getRole())
        .build();
  }
}
