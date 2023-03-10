package com.chaechae.realworldspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RealWorldSpringbootApplication {
	public static void main(String[] args) {
		SpringApplication.run(RealWorldSpringbootApplication.class, args);
	}
}
