package com.chaechae.realworldspringboot.config;

import com.chaechae.realworldspringboot.base.exception.handler.RestTemplateExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Configuration
public class ApplicationConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(new RestTemplateExceptionHandler(objectMapper()))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer()));
    }
}
