package com.orderprocessing.order_processing_shipments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(	scanBasePackages = "com.orderprocessing",
						exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableJpaRepositories(basePackages = "com.orderprocessing.ship.repositories")
@EntityScan(basePackages = "com.orderprocessing.ship.entities")
public class OrderProcessingShipmentsApplication {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		SpringApplication.run(OrderProcessingShipmentsApplication.class, args);
	}

}
