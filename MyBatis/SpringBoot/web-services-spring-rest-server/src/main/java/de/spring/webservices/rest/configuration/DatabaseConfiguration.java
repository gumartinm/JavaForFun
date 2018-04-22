package de.spring.webservices.rest.configuration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "de.spring.webservices.rest.persistence.repository",
            markerInterface = de.spring.webservices.rest.persistence.repository.BaseRepository.class)
public class DatabaseConfiguration {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:/db/migration/flyway/");
        flyway.baseline();
        flyway.migrate();

        return flyway;
    }
}
