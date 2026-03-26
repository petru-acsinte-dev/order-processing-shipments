package com.orderprocessing.order_processing_shipments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.orderprocessing")
@EnableJpaRepositories(basePackages = "com.orderprocessing.shipments.repositories")
@EntityScan(basePackages = "com.orderprocessing.shipments.entities")
@EnableFeignClients(basePackages = "com.orderprocessing.shipments.clients")
public class OrderProcessingShipmentsApplication {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		SpringApplication.run(OrderProcessingShipmentsApplication.class, args);
	}

}
