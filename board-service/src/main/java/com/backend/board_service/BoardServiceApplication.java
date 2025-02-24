package com.backend.board_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoardServiceApplication {
	static {
		Dotenv dotenv = Dotenv.configure()
				.directory("C:/dev/spring-boot/board-service/board-service")
				.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
	}

	public static void main(String[] args) {
		SpringApplication.run(BoardServiceApplication.class, args);
	}

}
