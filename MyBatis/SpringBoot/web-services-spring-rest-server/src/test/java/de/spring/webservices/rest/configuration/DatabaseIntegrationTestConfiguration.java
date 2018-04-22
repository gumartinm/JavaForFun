package de.spring.webservices.rest.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseIntegrationTestConfiguration {
    @Value("${database.port}")
    private int port;

    @Bean
    public DataSource dataSource() {
        String jdbConnection = String.format("jdbc:postgresql://localhost:%d/locationclosure", port);
        return DataSourceBuilder.create().username("locationclosure").password("locationclosure")
                .url(jdbConnection)
                .driverClassName("org.postgresql.Driver").build();
    }

}
