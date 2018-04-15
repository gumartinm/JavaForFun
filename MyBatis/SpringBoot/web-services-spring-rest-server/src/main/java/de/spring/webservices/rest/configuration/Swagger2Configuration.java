package de.spring.webservices.rest.configuration;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Go to URL: http://localhost:8080/swagger-ui.html#/
 *
 */
@Configuration
@EnableSwagger2
public class Swagger2Configuration {
	
	@Bean
	public Docket documentation() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
					.apis(withMethodAnnotation(RequestMapping.class))
					.apis(basePackage("de.spring.webservices.rest.controller"))
					.paths(PathSelectors.any())
					.build()
					.globalResponseMessage(RequestMethod.GET,
							newArrayList(new ResponseMessageBuilder()
									.code(500).message("Global server custom error message").build()))
		        .useDefaultResponseMessages(false)
		        .apiInfo(metadata())
		        .enable(true);
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration(null);
	}
	
	private static ApiInfo metadata() {
		return new ApiInfoBuilder()
				.title("gumartinm REST API")
				.description("Gustavo Martin Morcuende")
				.version("1.0-SNAPSHOT")
		        .contact(doContact())
		        .build();
	}

	private static Contact doContact() {
		return new Contact("Gustavo Martin", "https://gumartinm.name", "");
	}
}
