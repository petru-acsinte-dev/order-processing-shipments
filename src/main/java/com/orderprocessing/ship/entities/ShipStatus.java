package com.orderprocessing.ship.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "status", schema = "ship")
public class ShipStatus {

	@Id
	private Short id; // well known, predefined

	@NotEmpty
	private String status;

	protected ShipStatus() {} // JPA

	public ShipStatus(@PositiveOrZero Short id, @NotEmpty String status) {
		this.id = id;
		this.status = status;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
