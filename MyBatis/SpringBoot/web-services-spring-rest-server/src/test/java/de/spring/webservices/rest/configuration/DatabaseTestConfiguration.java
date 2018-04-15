package de.spring.webservices.rest.configuration;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseTestConfiguration {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().username("root").password("")
                .url("jdbc:h2:mem:dbo;INIT=create schema if not exists locationsclosure\\;SET SCHEMA locationsclosure;"
                        + "MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .driverClassName("org.h2.Driver").build();
    }

}
