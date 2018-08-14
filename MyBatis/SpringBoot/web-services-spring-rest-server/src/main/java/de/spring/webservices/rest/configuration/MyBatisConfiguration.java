package de.spring.webservices.rest.configuration;

import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

import de.spring.webservices.rest.persistence.repository.LocationsBaseRepository;
import de.spring.webservices.rest.persistence.repository.conciliation.ConciliationBaseRepository;

// You do not need any of this stuff if you have only one data source.
@Configuration
public class MyBatisConfiguration {
	private static final String SESSION_FACTORY_LOCATIONS = "sessionFactoryLocations";
	private static final String SESSION_FACTORY_CONCILIATION = "sessionFactoryConciliation";
	
	private static final String MAPPER_SCANNER_LOCATIONS = "mapperScannerLocations";
	private static final String MAPPER_SCANNER_CONCILIATION = "mapperScannerConciliation";

	private static final String SESSION_TEMPLATE_LOCATIONS = "sessionTemplateLocations";
	private static final String SESSION_TEMPLATE_CONCILIATION = "sessionTemplateConciliation";
	
	@Value("classpath:de/spring/webservices/rest/persistence/repository/*Repository.xml")
	Resource[] locationsMappers;
	
	@Value("classpath:de/spring/webservices/rest/persistence/repository/conciliation/*Repository.xml")
	Resource[] conciliationMappers;
	
	@Value("classpath:mybatis/mybatis-config.xml")
	Resource myBatisConfigLocation;

	@Bean(SESSION_FACTORY_LOCATIONS)
	@Primary
	public SqlSessionFactory sessionFactoryLocations(
			@Named(DatabaseConfiguration.DATA_SOURCE_LOCATIONS) DataSource dataSourceLocations) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSourceLocations);
		sqlSessionFactoryBean.setMapperLocations(locationsMappers);
		sqlSessionFactoryBean.setConfigLocation(myBatisConfigLocation);
		return sqlSessionFactoryBean.getObject();
	}

	@Bean(SESSION_TEMPLATE_LOCATIONS)
	@Primary
	public SqlSessionTemplate sessionTemplateLocations(@Named(SESSION_FACTORY_LOCATIONS) SqlSessionFactory sessionFactoryLocations) {
		return new SqlSessionTemplate(sessionFactoryLocations, ExecutorType.BATCH);
	}
	
	@Bean(MAPPER_SCANNER_LOCATIONS)
	@Primary
	public MapperScannerConfigurer mapperScannerLocations() {
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setBasePackage("de.spring.webservices.rest.persistence.repository");
		mapperScannerConfigurer.setMarkerInterface(LocationsBaseRepository.class);
		mapperScannerConfigurer.setSqlSessionTemplateBeanName(SESSION_TEMPLATE_LOCATIONS);
		return mapperScannerConfigurer;
	}



	@Bean(SESSION_FACTORY_CONCILIATION)
	public SqlSessionFactory sessionFactoryConciliation(
			@Named(DatabaseConfiguration.DATA_SOURCE_CONCILIATION) DataSource dataSourceConciliation) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSourceConciliation);
		sqlSessionFactoryBean.setMapperLocations(conciliationMappers);
		sqlSessionFactoryBean.setConfigLocation(myBatisConfigLocation);
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean(SESSION_TEMPLATE_CONCILIATION)
	public SqlSessionTemplate sessionTemplateConciliation(@Named(SESSION_FACTORY_CONCILIATION) SqlSessionFactory sessionFactoryLocations) {
		return new SqlSessionTemplate(sessionFactoryLocations, ExecutorType.BATCH);
	}
	
	@Bean(MAPPER_SCANNER_CONCILIATION)
	public MapperScannerConfigurer mapperScannerConciliation() {
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setBasePackage("de.spring.webservices.rest.persistence.repository.conciliation");
		mapperScannerConfigurer.setMarkerInterface(ConciliationBaseRepository.class);
		mapperScannerConfigurer.setSqlSessionTemplateBeanName(SESSION_TEMPLATE_CONCILIATION);
		return mapperScannerConfigurer;
	}
}
