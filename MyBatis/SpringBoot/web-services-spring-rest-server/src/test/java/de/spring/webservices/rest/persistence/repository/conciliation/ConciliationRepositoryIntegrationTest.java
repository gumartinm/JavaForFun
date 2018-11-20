package de.spring.webservices.rest.persistence.repository.conciliation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import de.spring.webservices.domain.conciliation.Layer;
import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.configuration.DatabaseIntegrationTestConfiguration;
import de.spring.webservices.rest.configuration.MyBatisConfiguration;
import de.spring.webservices.rest.persistence.repository.conciliation.ConciliationRepository;

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
@DbUnitConfiguration(databaseConnection = { "dbUnitConciliationConciliationSchema",
                                            "dbUnitConciliationApplicationSchema" })
public class ConciliationRepositoryIntegrationTest {

    @Inject
	ConciliationRepository conciliationRepository;

    @Test
	@DatabaseSetup(connection = "dbUnitConciliationApplicationSchema",
	               value = { "/db/dbunit/conciliation/application/countries.xml" })
	@DatabaseSetup(connection = "dbUnitConciliationConciliationSchema",
    			   value = { "/db/dbunit/conciliation/conciliation/conciliation.xml" })
    public void findAll() {
		List<Layer> layers = conciliationRepository.findAll();
		Layer layer = layers.get(0);

		assertThat(layer.getName(), is("Spain"));
    }
}
