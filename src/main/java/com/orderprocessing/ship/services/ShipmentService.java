package com.orderprocessing.ship.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderprocessing.common.constants.Constants;
import com.orderprocessing.common.exceptions.UnauthorizedOperationException;
import com.orderprocessing.common.security.SecurityUtils;
import com.orderprocessing.ship.constants.Status;
import com.orderprocessing.ship.dto.ShipmentResponse;
import com.orderprocessing.ship.entities.ShipStatus;
import com.orderprocessing.ship.entities.Shipment;
import com.orderprocessing.ship.exceptions.OrderAlreadyShippedException;
import com.orderprocessing.ship.exceptions.ShipmentNotFoundException;
import com.orderprocessing.ship.exceptions.ShippingStatusNotFoundException;
import com.orderprocessing.ship.mappers.ShipmentMapper;
import com.orderprocessing.ship.props.ShipProps;
import com.orderprocessing.ship.repositories.ShipStatusRepository;
import com.orderprocessing.ship.repositories.ShipmentRepository;

@Service
public class ShipmentService {

	private final ShipmentRepository repository;

	private final ShipStatusRepository statusRepository;

	private final ShipmentMapper mapper;

	private final ShipProps shipProps;

	private final ApplicationEventPublisher publisher;

	public ShipmentService(ShipmentRepository repository, ShipStatusRepository statusRepository, ShipmentMapper mapper,
			ShipProps shipProps, ApplicationEventPublisher publisher) {
		this.repository = repository;
		this.statusRepository = statusRepository;
		this.mapper = mapper;
		this.shipProps = shipProps;
		this.publisher = publisher;
	}

	/**
	 * Returns the shipments in paged responses. By default the newest shipments are first.
	 * @param pageable Pagination and sorting information.
	 * @return A page containing {@link ShipmentResponse} DTOs with shipment basic details.
	 * @throws UnauthorizedOperationException if the user making the request does not have access to the shipments.
	 */
	@Transactional(readOnly = true)
	public Page<ShipmentResponse> getShipments(Pageable pageable) {
		SecurityUtils.confirmAdminRole();

		final Pageable pagingRequest = getPagingRequest(pageable);

		final Page<Shipment> page = repository.findAll(pagingRequest);

		return page.map(mapper::toResponse);
	}

	/**
	 * Returns the shipment for a specific order.
	 * @param orderExternalId Order unique external identifier.
	 * @return A page containing {@link ShipmentResponse} DTOs with shipment basic details.
	 * @throws UnauthorizedOperationException if the user making the request does not have access to the shipments.
	 */
	@Transactional(readOnly = true)
	public ShipmentResponse getOrderShipment(@RequestParam(required = true) UUID orderExternalId) {

		SecurityUtils.confirmAdminRole();

		final Shipment shipment = repository.findByOrderExternalId(orderExternalId)
			.orElseThrow(() -> new ShipmentNotFoundException(orderExternalId));

		return mapper.toResponse(shipment);
	}

	/**
	 * Creates a shipment record for an order.
	 * @param orderExternalId The unique order external id.
	 * @return The shipment details.
	 */
	@Transactional
	public ShipmentResponse createShipment(UUID orderExternalId) {
		final Optional<Shipment> shipment = repository.findByOrderExternalId(orderExternalId);

		if (shipment.isPresent()) {
			throw new OrderAlreadyShippedException(orderExternalId);
		}

		final ShipStatus status = statusRepository.findByStatus(Status.SHIPPED.name())
				.orElseThrow(() -> new ShippingStatusNotFoundException(Status.SHIPPED.name()));

		final Shipment newShipment = new Shipment();
		newShipment.setExternalId(UUID.randomUUID());
		newShipment.setStatus(status);
		newShipment.setOrderExternalId(orderExternalId);

		final Shipment created = repository.save(newShipment);

		// FIXME: Convert to a Kafka/RabbitMQ event once shipment service and a message broker are available
//		publisher.publishEvent(new OrderShippedEvent(orderExternalId));

		return mapper.toResponse(created);
	}

	private Pageable getPagingRequest(Pageable pageable) {
		int pageNo = 0;
		int pageSize = shipProps.getPageSize();
		final String sort = shipProps.getShipmentSortingAttribute();
		Sort sortBy = null;
		if (null != sort) {
			final String[] split = sort.split(","); //$NON-NLS-1$
			if (split.length > 1) {
				sortBy = Sort.by(split[0]).descending();
			} else {
				sortBy = Sort.by(split[0]);
			}
		}

		if (null != pageable) {
			if (pageable.getPageNumber() > 0) {
				pageNo = pageable.getPageNumber();
			}
			final int requestSize = pageable.getPageSize();
			if (requestSize > 0
			&& requestSize <= Constants.PAGE_SIZE_HARD_LIMIT // system imposed limit
			&& requestSize <= shipProps.getMaxPageSize()) {
				pageSize = requestSize;
			}
			if (null == sortBy) {
				sortBy = pageable.getSort();
			} else {
				sortBy = pageable.getSortOr(sortBy);
			}
		}
		if (null == sortBy) {
			return PageRequest.of(pageNo, pageSize);
		}
		return PageRequest.of(pageNo, pageSize, sortBy);
	}
}
