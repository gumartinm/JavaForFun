package de.spring.webservices.rest.persistence.repository;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
//import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import de.spring.webservices.domain.LocationType;
import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.configuration.DatabaseIntegrationTestConfiguration;
import de.spring.webservices.rest.configuration.MyBatisConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DatabaseConfiguration.class,
                            DatabaseIntegrationTestConfiguration.class,
                            // MybatisAutoConfiguration.class,
                            MyBatisConfiguration.class })
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@OverrideAutoConfiguration(enabled = false)
@Transactional(DatabaseConfiguration.TRX_MANAGER_LOCATIONS)
@AutoConfigureCache
//@AutoConfigureMybatis
@ImportAutoConfiguration
// @MybatisTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
                          DirtiesContextTestExecutionListener.class,
                          TransactionDbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection = { "dbUnitLocations" })
public class LocationTypeRepositoryIntegrationTest {

    @Inject
	LocationTypeRepository locationTypeRepository;

	@Test(expected = DuplicateKeyException.class)
	@DatabaseSetup(connection = "dbUnitLocations",
	               value = { "/db/dbunit/location_types.xml", "/db/dbunit/locations.xml" })
	public void save() {
		LocationType locationType = new LocationType(3, "CONTINENT");
		locationTypeRepository.save(locationType);

		List<LocationType> locationTypes = locationTypeRepository.findAll();
    }
}
