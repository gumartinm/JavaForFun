package de.spring.webservices.rest.configuration;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@MapperScan(basePackages = "de.spring.webservices.rest.persistence.repository",
            markerInterface = de.spring.webservices.rest.persistence.repository.BaseRepository.class)
public class DatabaseConfiguration {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase springLiquibase = new SpringLiquibase();
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setChangeLog("classpath:/liquibase/changeLogs.xml");
        return springLiquibase;
    }
}
