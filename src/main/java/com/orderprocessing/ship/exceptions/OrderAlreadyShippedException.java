package com.orderprocessing.ship.exceptions;

import java.util.UUID;

import com.orderprocessing.common.exceptions.ApiErrors;
import com.orderprocessing.common.exceptions.BadRequestApiException;

public class OrderAlreadyShippedException extends BadRequestApiException {

	private static final long serialVersionUID = 1L;

	public OrderAlreadyShippedException(UUID orderExternalId) {
		super(ApiErrors.ORDER_ALREADY_SHIPPED, MessageKeys.ORDER_ALREADY_SHIPPED, orderExternalId);
	}

}
