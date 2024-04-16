package com.naatsms.payment;

import com.naatsms.payment.service.NotificationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

@TestConfiguration(proxyBeanMethods = false)
public class TestPaymentApplication {

	@Bean
	@Profile("test")
	NotificationService notificationService() {
		return transaction -> Mono.empty();
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}

	public static void main(String[] args) {
		SpringApplication.from(PaymentApplication::main).with(TestPaymentApplication.class).run(args);
	}

}
