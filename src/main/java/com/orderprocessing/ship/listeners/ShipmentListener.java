package com.orderprocessing.ship.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.orderprocessing.ship.events.OrderFulfilledEvent;
import com.orderprocessing.ship.messaging.ShipmentEventPublisher;
import com.orderprocessing.ship.services.ShipmentService;

@Component
public class ShipmentListener {

	Logger log = LoggerFactory.getLogger(ShipmentListener.class);

	private final ShipmentService service;

	private final ShipmentEventPublisher eventPublisher;

	public ShipmentListener(ShipmentService service, ShipmentEventPublisher publisher) {
		this.service = service;
		this.eventPublisher = publisher;
	}

	@EventListener
	public void handle(OrderFulfilledEvent event) {
	    service.createShipment(event.orderExternalId());
	    eventPublisher.publishOrderShippedEvent(event.orderExternalId());
	    // TODO: Transactional Outbox pattern needed here
	    // publish failure leaves shipment saved but order unnotified
	}
}
