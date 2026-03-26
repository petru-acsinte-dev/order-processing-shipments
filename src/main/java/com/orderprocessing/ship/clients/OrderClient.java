package com.orderprocessing.ship.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order", url = "${orders.service.url}")
public interface OrderClient {

	@PostMapping("/{orderId}/ship")
    ResponseEntity<Void> confirmShipped(@PathVariable UUID externalId);

}
