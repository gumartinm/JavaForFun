package de.example.plugins.custom.java;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.plugins.java.Java;

public class CustomSensor implements Sensor {

	@Override
	public void describe(SensorDescriptor descriptor) {
	    descriptor.onlyOnLanguage(Java.KEY);
	    descriptor.name("Custom Sensor");
	}

	@Override
	public void execute(SensorContext context) {
		
	}

}
