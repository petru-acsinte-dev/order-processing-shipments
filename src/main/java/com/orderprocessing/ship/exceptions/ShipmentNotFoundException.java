package com.orderprocessing.ship.exceptions;

import java.util.UUID;

import com.orderprocessing.common.exceptions.ApiErrors;
import com.orderprocessing.common.exceptions.NotFoundApiException;

public class ShipmentNotFoundException extends NotFoundApiException {

	private static final long serialVersionUID = 1L;

	public ShipmentNotFoundException(UUID externalOrderId) {
		super(ApiErrors.SHIPMENT_NOT_FOUND, MessageKeys.SHIPMENT_NOT_FOUND, externalOrderId);
	}

}
