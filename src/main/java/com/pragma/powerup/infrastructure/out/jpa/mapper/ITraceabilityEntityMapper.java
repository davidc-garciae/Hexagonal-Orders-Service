package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.TraceabilityEvent;
import com.pragma.powerup.infrastructure.out.jpa.entity.TraceabilityEventEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ITraceabilityEntityMapper {

  @Mapping(target = "id", ignore = true) // MongoDB will generate the ID
  @Mapping(target = "clientEmail", ignore = true) // Not used in current domain model
  @Mapping(target = "eventDate", source = "timestamp")
  @Mapping(target = "additionalNotes", ignore = true) // Not used in current domain model
  @Mapping(target = "estimatedDeliveryTime", ignore = true) // Not used in current domain model
  @Mapping(target = "employeeName", source = "employeeName") // Now mapping employeeName
  TraceabilityEventEntity toEntity(TraceabilityEvent traceabilityEvent);

  @Mapping(target = "eventType", ignore = true) // Not stored in entity
  @Mapping(target = "timestamp", source = "eventDate")
  @Mapping(target = "employeeName", source = "employeeName") // Now mapping employeeName back
  TraceabilityEvent toDomainModel(TraceabilityEventEntity entity);

  List<TraceabilityEvent> toDomainModelList(List<TraceabilityEventEntity> entities);

  List<TraceabilityEventEntity> toEntityList(List<TraceabilityEvent> traceabilityEvents);
}
