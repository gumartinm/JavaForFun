package de.spring.webservices.rest.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseIntegrationTestConfiguration {
    @Value("${database-locations.port:5432}")
    private int locationsPort;

    @Value("${database-conciliation.port:5433}")
    private int conciliationPort;

	@Bean(name = DatabaseConfiguration.DATA_SOURCE_LOCATIONS)
	@ConfigurationProperties(prefix = "datasources.locations")
	@Primary
	public DataSource dataSourceLocations() {
        String jdbConnection = String.format("jdbc:postgresql://localhost:%d/locations", locationsPort);
        return DataSourceBuilder.create().username("locations").password("locations")
                .url(jdbConnection)
                .driverClassName("org.postgresql.Driver").build();
	}

	@Bean(name = DatabaseConfiguration.DATA_SOURCE_CONCILIATION)
	@ConfigurationProperties(prefix = "datasources.conciliation")
	public DataSource dataSourceConciliation() {
        String jdbConnection = String.format("jdbc:postgresql://localhost:%d/conciliation", conciliationPort);
        return DataSourceBuilder.create().username("conciliation").password("conciliation")
                .url(jdbConnection)
                .driverClassName("org.postgresql.Driver").build();
	}

}
