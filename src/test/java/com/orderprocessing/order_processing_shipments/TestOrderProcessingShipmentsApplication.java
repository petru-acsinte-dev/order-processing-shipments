package com.orderprocessing.order_processing_shipments;

import org.springframework.boot.SpringApplication;

public class TestOrderProcessingShipmentsApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderProcessingShipmentsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
