package ru.practicum.shareit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
public class AppConfig {
}


/*
spring.datasource.driver-class-name=org.postgresql.Driver
        spring.jpa.database=postgresql
        spring.datasource.url=jdbc:postgresql://localhost:5432/shareit
        spring.datasource.username=sharit
        spring.datasource.password=1234
        spring.datasource.initialization-mode=ALWAYS
        spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect*/
