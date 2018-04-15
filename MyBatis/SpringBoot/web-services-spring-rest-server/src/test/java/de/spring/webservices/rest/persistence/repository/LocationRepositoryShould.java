package de.spring.webservices.rest.persistence.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.spring.webservices.domain.Location;
import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.configuration.DatabaseTestConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DatabaseConfiguration.class,
                            DatabaseTestConfiguration.class,
                            MybatisAutoConfiguration.class })
@MybatisTest
public class LocationRepositoryShould {

    @Inject
    LocationRepository locationRepository;

    @Test
    public void findAll() {
        List<Location> locations = locationRepository.findAll();
        Location location = locations.get(0);

        assertThat(location.getDescription(), is("Spain"));
    }

}
