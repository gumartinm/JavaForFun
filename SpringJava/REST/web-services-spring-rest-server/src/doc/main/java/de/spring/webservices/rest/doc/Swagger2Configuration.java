package de.spring.webservices.rest.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class Swagger2Configuration {
	private static final String DOCKET_ID = "web-services-spring-rest";
	
	@Bean
	public Docket documentation() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName(DOCKET_ID)
				.select()
					.apis(RequestHandlerSelectors.withMethodAnnotation(RequestMapping.class))
					.paths(PathSelectors.any())
					.build()
		        .pathMapping("/")
		        .useDefaultResponseMessages(false)
		        .apiInfo(metadata())
		        .enable(true);
	}

	@Bean
	UiConfiguration uiConfig() {
		return UiConfiguration.DEFAULT;
	}

	
	private static ApiInfo metadata() {
		return new ApiInfoBuilder()
				.title("gumartinm REST API")
				.description("Gustavo Martin Morcuende")
				.version("1.0-SNAPSHOT")
		        .contact("gumartinm.name")
		        .build();
	}

}
