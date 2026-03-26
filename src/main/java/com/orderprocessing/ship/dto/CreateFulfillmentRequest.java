package com.orderprocessing.ship.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * DTO used to signal fulfillment service that an order has been confirmed.
 */
public class CreateFulfillmentRequest {

	@NotNull
    private UUID orderExternalId;

	public CreateFulfillmentRequest(@NotNull UUID orderExternalId) {
		this.orderExternalId = orderExternalId;
	}

	public UUID getOrderExternalId() {
		return orderExternalId;
	}

	public void setOrderExternalId(UUID orderExternalId) {
		this.orderExternalId = orderExternalId;
	}

}
