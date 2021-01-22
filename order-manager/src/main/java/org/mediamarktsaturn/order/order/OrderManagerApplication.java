package org.mediamarktsaturn.order.order;

import org.mediamarktsaturn.order.order.rest.OrderManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("org.mediamarktsaturn.order.order")
public class OrderManagerApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(OrderManagerApplication.class, args);
		OrderManagerServer server = applicationContext.getBean(OrderManagerServer.class);
		server.startHttpServer();
	}
}
