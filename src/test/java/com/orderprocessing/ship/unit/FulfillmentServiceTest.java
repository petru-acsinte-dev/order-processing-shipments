package com.orderprocessing.ship.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.orderprocessing.common.tests.AbstractUnitTestBase;
import com.orderprocessing.ship.constants.Status;
import com.orderprocessing.ship.dto.FulfillmentResponse;
import com.orderprocessing.ship.entities.Fulfillment;
import com.orderprocessing.ship.entities.ShipStatus;
import com.orderprocessing.ship.mappers.FulfillmentMapper;
import com.orderprocessing.ship.props.ShipProps;
import com.orderprocessing.ship.repositories.FulfillmentRepository;
import com.orderprocessing.ship.repositories.ShipStatusRepository;
import com.orderprocessing.ship.services.FulfillmentService;

public class FulfillmentServiceTest extends AbstractUnitTestBase {

	@Mock
	private FulfillmentRepository repository;

	@Mock
	private ShipStatusRepository statusRepository;

	@Mock
	private FulfillmentMapper mapper;

	@Mock
	private ShipProps shipProps;

	@InjectMocks
	private FulfillmentService service;

	@Test
	@DisplayName("Creates a new fulfillment")
	void testCreateNewFulfillment() {
		final UUID orderId = UUID.randomUUID();

		given(repository.findByOrderExternalId(orderId))
			.willReturn(Optional.empty());

		final ShipStatus ready = new ShipStatus(Status.READY_TO_SHIP.getId(), Status.READY_TO_SHIP.name());

		given(statusRepository.findByStatus(Status.READY_TO_SHIP.name()))
			.willReturn(Optional.of(ready));

		final var fulfillment = new Fulfillment();
		fulfillment.setOrderExternalId(orderId);
		fulfillment.setStatus(ready);

		final FulfillmentResponse expected = new FulfillmentResponse();
		expected.setOrderExternalId(orderId);
		expected.setStatus(ready.getStatus());

		given(mapper.toResponse(fulfillment))
			.willReturn(expected);

		given(repository.save(any()))
			.willReturn(fulfillment);

		final FulfillmentResponse newFulfillment = service.createFulfilment(orderId);

		assertThat(orderId).isEqualTo(newFulfillment.getOrderExternalId());
		assertThat(ready.getStatus()).isEqualTo(newFulfillment.getStatus());
	}

}
