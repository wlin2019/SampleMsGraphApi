package com.ms360service.graphAPI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GraphApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphApiApplication.class, args);
	}

	@Bean
	protected CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> System.out.println("graphAPI service started.");
	}
}
