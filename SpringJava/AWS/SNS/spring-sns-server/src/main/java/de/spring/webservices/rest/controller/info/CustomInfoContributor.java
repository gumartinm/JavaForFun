package de.spring.webservices.rest.controller.info;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;

public class CustomInfoContributor implements InfoContributor {

	@Override
	public void contribute(Builder builder) {
		builder.withDetail("custominfo", "INFORMATION FOR DEVOPS!!!");
	}

}
