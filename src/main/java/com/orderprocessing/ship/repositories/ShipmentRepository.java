package com.orderprocessing.ship.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orderprocessing.ship.entities.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

	@Override
	Page<Shipment> findAll(Pageable pagingRequest);

	Optional<Shipment> findByOrderExternalId(UUID orderExternalId);

}
