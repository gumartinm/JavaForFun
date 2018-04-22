package de.spring.webservices.rest.persistence.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import de.spring.webservices.domain.Location;
import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.configuration.DatabaseIntegrationTestConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DatabaseConfiguration.class,
                            DatabaseIntegrationTestConfiguration.class,
                            MybatisAutoConfiguration.class })
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@OverrideAutoConfiguration(enabled = false)
@Transactional
@AutoConfigureCache
@AutoConfigureMybatis
@ImportAutoConfiguration
// @MybatisTest
public class LocationRepositoryIntegrationTest {

    @Inject
    LocationRepository locationRepository;

    @Test
    public void findAll() {
        List<Location> locations = locationRepository.findAll();
        Location location = locations.get(0);

        assertThat(location.getDescription(), is("Spain"));
    }

}
