package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {

  @Mapping(target = "items", ignore = true)
  OrderEntity toEntity(Order model);

  @Mapping(target = "items", ignore = true)
  Order toModel(OrderEntity entity);

  @AfterMapping
  default void linkItems(@MappingTarget OrderEntity entity) {
    if (entity.getItems() != null) {
      entity.getItems().forEach(i -> i.setOrder(entity));
    }
  }
}
