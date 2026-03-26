package com.orderprocessing.ship.exceptions;

import java.util.UUID;

import com.orderprocessing.common.exceptions.ApiErrors;
import com.orderprocessing.common.exceptions.NotFoundApiException;

public class FulfillmentNotFoundException extends NotFoundApiException {

	private static final long serialVersionUID = 1L;

	public FulfillmentNotFoundException(UUID orderExternalId) {
		super(ApiErrors.FULFILLMENT_NOT_FOUND, MessageKeys.FULFILLMENT_NOT_FOUND, orderExternalId);
	}

}
