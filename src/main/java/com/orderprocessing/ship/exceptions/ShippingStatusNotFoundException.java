package com.orderprocessing.ship.exceptions;

import org.springframework.http.HttpStatus;

import com.orderprocessing.common.exceptions.ApiErrors;
import com.orderprocessing.common.exceptions.ApiException;

public class ShippingStatusNotFoundException extends ApiException {

	private static final long serialVersionUID = 1L;

	public ShippingStatusNotFoundException(String status) {
		super(	HttpStatus.INTERNAL_SERVER_ERROR,
				ApiErrors.SHIPMENT_STATUS_NOT_FOUND,
				MessageKeys.SHIPMENT_STATUS_NOT_FOUND,
				status
		);
	}
}
