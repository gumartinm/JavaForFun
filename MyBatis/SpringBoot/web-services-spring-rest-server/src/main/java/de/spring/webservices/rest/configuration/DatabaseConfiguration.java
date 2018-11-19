package de.spring.webservices.rest.configuration;

import javax.inject.Named;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
// import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
// When dealing with multiple data sources we can not user @MapperScan :(
// @MapperScan(basePackages = "de.spring.webservices.rest.persistence.repository",
//             markerInterface = de.spring.webservices.rest.persistence.repository.LocationsBaseRepository.class)
public class DatabaseConfiguration {
	public static final String DATA_SOURCE_LOCATIONS = "dataSourceLocations";
	public static final String DATA_SOURCE_CONCILIATION = "dataSourceConciliation";

	public static final String TRX_MANAGER_LOCATIONS = "trxManagerLocations";
	public static final String TRX_MANAGER_CONCILIATION = "trxManagerConciliation";

	public static final String FLYWAY_LOCATIONS = "flywayLocations";
	public static final String FLYWAY_CONCILIATION = "flywayConciliation";

	@Bean(DATA_SOURCE_LOCATIONS)
	@ConfigurationProperties(prefix = "datasources.locations")
	@Primary
	public DataSource dataSourceLocations() {
		return DataSourceBuilder.create().build();
	}

	@Bean(TRX_MANAGER_LOCATIONS)
	DataSourceTransactionManager locationsTrxManager(@Named (DATA_SOURCE_LOCATIONS) DataSource dataSourceLocations) {
	    return new DataSourceTransactionManager(dataSourceLocations);
	}

	@Bean(FLYWAY_LOCATIONS)
    public Flyway flywayLocations(@Named(DATA_SOURCE_LOCATIONS) DataSource dataSourceLocations) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceLocations);
        flyway.setLocations("classpath:/db/migration/locations/flyway/");
		flyway.baseline();
        flyway.migrate();

        return flyway;
    }




	@Bean(DATA_SOURCE_CONCILIATION)
	@ConfigurationProperties(prefix = "datasources.conciliation")
	public DataSource dataSourceConciliation() {
		return DataSourceBuilder.create().build();
	}

	@Bean(TRX_MANAGER_CONCILIATION)
	DataSourceTransactionManager conciliationTrxManager(@Named (DATA_SOURCE_CONCILIATION) DataSource dataSourceConciliation) {
		return new DataSourceTransactionManager(dataSourceConciliation);
	}

	@Bean(FLYWAY_CONCILIATION)
    public Flyway flywayConciliation(@Named(DATA_SOURCE_CONCILIATION) DataSource dataSourceConciliation) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceConciliation);
        flyway.setLocations("classpath:/db/migration/conciliation/flyway/");
		flyway.baseline();
        flyway.migrate();

        return flyway;
    }
}
