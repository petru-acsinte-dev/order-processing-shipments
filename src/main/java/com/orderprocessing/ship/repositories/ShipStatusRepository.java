package com.orderprocessing.ship.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orderprocessing.ship.entities.ShipStatus;

@Repository
public interface ShipStatusRepository extends JpaRepository<ShipStatus, Short> {

	Optional<ShipStatus> findByStatus(String status);

}
