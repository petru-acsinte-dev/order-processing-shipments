package com.orderprocessing.ship.events;

import java.util.UUID;

/**
 * Event used to announce the confirmation of an order.
 */
public record OrderShippedEvent(UUID orderExternalId) {}