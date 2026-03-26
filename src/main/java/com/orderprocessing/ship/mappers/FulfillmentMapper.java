package com.orderprocessing.ship.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.orderprocessing.common.mappers.GlobalMapperConfig;
import com.orderprocessing.ship.dto.FulfillmentResponse;
import com.orderprocessing.ship.entities.Fulfillment;

@Mapper(config = GlobalMapperConfig.class)
public interface FulfillmentMapper {

	@Mapping(target = "status", source = "fulfillment", qualifiedByName = "mapShipStatus")
	FulfillmentResponse toResponse(Fulfillment fulfillment);

	@Named("mapShipStatus")
	default String mapShipStatus(Fulfillment entity) {
		return entity.getStatus().getStatus();
	}

}
