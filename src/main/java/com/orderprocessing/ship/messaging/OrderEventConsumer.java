package com.orderprocessing.ship.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.orderprocessing.common.configurations.RabbitMQConfig;
import com.orderprocessing.common.constants.Constants;
import com.orderprocessing.common.events.OrderConfirmedEvent;
import com.orderprocessing.ship.services.FulfillmentService;

@Component
public class OrderEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

	private final FulfillmentService fulfillmentService;

	public OrderEventConsumer(FulfillmentService fulfillmentService) {
		this.fulfillmentService = fulfillmentService;
	}

	@RabbitListener(queues = RabbitMQConfig.SHIPMENT_ORDER_CONFIRMED_QUEUE)
	public void consumeConfirmedOrderEvent(OrderConfirmedEvent event) {
		MDC.put(Constants.CORRELATION_ID, event.correlationId());
		try {
			log.info("Received an OrderConfirmedEvent for {}", event.orderExternalId()); //$NON-NLS-1$
			fulfillmentService.createFulfilment(event.orderExternalId());
		} finally {
			MDC.remove(Constants.CORRELATION_ID);
		}
	}

}
