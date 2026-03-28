package com.orderprocessing.ship.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.orderprocessing.common.mappers.GlobalMapperConfig;
import com.orderprocessing.ship.dto.ShipmentResponse;
import com.orderprocessing.ship.entities.Shipment;

@Mapper(config = GlobalMapperConfig.class)
public interface ShipmentMapper extends DateTimeMapper {

	@Mapping(target = "status", source = "shipment", qualifiedByName = "mapShipStatus")
	ShipmentResponse toResponse(Shipment shipment);

	@Named("mapShipStatus")
	default String mapShipStatus(Shipment entity) {
		return entity.getStatus().getStatus();
	}

}
