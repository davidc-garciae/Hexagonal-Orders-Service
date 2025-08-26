package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderRequestMapper {
    Order toModel(OrderCreateRequestDto dto);
}
