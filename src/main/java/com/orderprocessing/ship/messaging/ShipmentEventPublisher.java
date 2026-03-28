package com.orderprocessing.ship.messaging;

import static com.orderprocessing.common.configurations.RabbitMQConfig.EXCHANGE;
import static com.orderprocessing.common.configurations.RabbitMQConfig.ORDER_SHIPPED_KEY;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.orderprocessing.common.events.OrderShippedEvent;

@Component
public class ShipmentEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(ShipmentEventPublisher.class);

	private final RabbitTemplate rabbitTemplate;

	public ShipmentEventPublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publishOrderShippedEvent(UUID externalOrderId) {
		final OrderShippedEvent event = new OrderShippedEvent(externalOrderId);
		log.info("Publishing shipping event for order {}", externalOrderId); //$NON-NLS-1$
		rabbitTemplate.convertAndSend(EXCHANGE, ORDER_SHIPPED_KEY, event);
	}
}
