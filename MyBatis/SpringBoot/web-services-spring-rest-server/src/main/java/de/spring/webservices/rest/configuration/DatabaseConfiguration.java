package de.spring.webservices.rest.configuration;

import javax.inject.Named;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@MapperScan(basePackages = "de.spring.webservices.rest.persistence.repository",
            markerInterface = de.spring.webservices.rest.persistence.repository.BaseRepository.class)
public class DatabaseConfiguration {
	public static final String DATA_SOURCE_LOCATIONS = "dataSourceLocations";
	public static final String DATA_SOURCE_CONCILIATION = "dataSourceConciliation";

	@Bean(name = DATA_SOURCE_LOCATIONS)
	@ConfigurationProperties(prefix = "datasources.locations")
	@Primary
	public DataSource dataSourceLocations() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = DATA_SOURCE_CONCILIATION)
	@ConfigurationProperties(prefix = "datasources.conciliation")
	public DataSource dataSourceConciliation() {
		return DataSourceBuilder.create().build();
	}

    @Bean
    public Flyway flywayLocations(@Named(DatabaseConfiguration.DATA_SOURCE_LOCATIONS) DataSource dataSourceLocations) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceLocations);
        flyway.setLocations("classpath:/db/migration/locations/flyway/");
        flyway.baseline();
        flyway.migrate();

        return flyway;
    }

    @Bean
    public Flyway flywayConciliation(@Named(DatabaseConfiguration.DATA_SOURCE_CONCILIATION) DataSource dataSourceConciliation) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceConciliation);
        flyway.setLocations("classpath:/db/migration/conciliation/flyway/");
        flyway.baseline();
        flyway.migrate();

        return flyway;
    }
}
