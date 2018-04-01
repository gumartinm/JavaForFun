package de.spring.example.persistence.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import de.spring.example.persistence.converters.DateToOffsetDateTimeConverter;
import de.spring.example.persistence.converters.OffsetDateTimeToDateConverter;

// Reactive Mongo configuration does not work with XML configuration (Spring developers forgot to implement XML configuration, only Java configuration is available :( )
@Configuration
@PropertySource("classpath:mongo.properties")
@EnableReactiveMongoRepositories(basePackages = "de.spring.example.persistence.repository")
@EnableMongoAuditing
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
	
	@Value("${mongo.host}")
	private String host;
	
	@Value("${mongo.port}")
	private Integer port;
	
	@Value("${mongo.database-name}")
	private String databaseName;
	  
	@Bean
	@Override
	public MongoClient reactiveMongoClient() {
		SocketSettings socketSettings = SocketSettings.builder()
										.connectTimeout(30000, TimeUnit.MILLISECONDS)
										.readTimeout(30000, TimeUnit.MILLISECONDS)
										.build();

		ServerAddress serverAddress = new ServerAddress(host, port);
		ClusterSettings clusterSettings = ClusterSettings.builder()
											.hosts(Collections.singletonList(serverAddress))
											.build();
		
		ConnectionPoolSettings connectionPoolSettings = ConnectionPoolSettings.builder()
										.maxWaitTime(15000, TimeUnit.MILLISECONDS)
										.build();
		
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
								.applicationName(databaseName)
								.socketSettings(socketSettings).clusterSettings(clusterSettings)
								.connectionPoolSettings(connectionPoolSettings)
								.build();
		return MongoClients.create(mongoClientSettings);
	}
	
	@Bean
	@Override
	public ReactiveMongoTemplate reactiveMongoTemplate() throws Exception {
		ReactiveMongoTemplate reactiveMongoTemplate = new ReactiveMongoTemplate(reactiveMongoDbFactory(), super.mappingMongoConverter());
		reactiveMongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
		
		return reactiveMongoTemplate;
	}

	@Override
	protected String getDatabaseName() {
		return databaseName;
	}

	// IT DOES NOT WORK!!!
	// I THINK THERE IS A BUG BECAUSE MappingMongoConverter.this.conversionService never includes my custom converters!!!!
	@Bean
	@Override
	public CustomConversions customConversions() {
	    List<Converter<?, ?>> converterList = new ArrayList<>();
	    converterList.add(new OffsetDateTimeToDateConverter());
	    converterList.add(new DateToOffsetDateTimeConverter());

	    return new MongoCustomConversions(converterList);
	}
}