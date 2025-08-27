package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.OrderPageResponseDto;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.PagedResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {IOrderResponseMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderPageResponseMapper {

  @Mapping(target = "content", source = "content")
  OrderPageResponseDto toResponse(PagedResult<Order> paged);
}
