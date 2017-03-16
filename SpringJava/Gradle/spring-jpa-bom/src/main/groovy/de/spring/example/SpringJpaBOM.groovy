package de.spring.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.publish.maven.MavenPublication;
	

/**
 * No way of easily sharing a build.gradle script (uploading it to some artifactory repository) 
 * 
 * It seems like the best option is creating a gradle pluin. 
 *
 * This gradle plugin is a BOM (build of materials)
 *  
 * See: http://stackoverflow.com/questions/9539986/how-to-share-a-common-build-gradle-via-a-repository
 * 
 */
class SpringJpaBOM implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		
		
		// *****************   USING PROPERTIES FOR VERSIONING, WE MANAGE FROM HERE THE SUBPROJECTS' DEPENDENCIES   *****************
		project.ext {			
		
			// Compile dependencies
			// Spring
			springVersion = '4.3.0.RELEASE'
			// Spring JPA
			springJPAVersion = '1.10.2.RELEASE'
			// Spring Envers
			springDataEnversVersion = '1.0.2.RELEASE'
			hibernateEnversVersion = '5.2.1.Final'
			// Hibernate
			hibernateValidatorVersion = '5.2.4.Final'
			hibernateEntityManagerVersion = '5.2.1.Final'
			// QueryDSL
			queryDslAptVersion = '4.1.3'
			// Liquibase or Flyway
			liquibaseVersion = '3.5.3'
			flyWayVersion = '4.0.3'
			// Jackson.
			jacksonVersion = '2.8.1'
			// Validation API. JSR-303
			validationAPIVersion = '1.1.0.Final'
		
			// Provided dependencies.
			// Provided dependencies are compileOnly in Gradle. Unlike maven they are not included on the test classpath. Gradle sucks?!
			servletApiVersion = '4.0.0-b01'
		
			// LOG4J2 dependencies
			slf4jVersion = '2.7'
			log4jCoreVersion = '2.7'
			jclOverSlf4jVersion = '1.7.22'
		
			// Unit test dependencies
			dockerComposeRuleVersion = '0.28.1'
			mockitoVersion = '2.4.1'
			junitVersion = '4.12'
		
			// Integration test dependencies
			h2DatabaseVersion = '1.4.193'
			mysqlConnectorVersion = '6.0.5'
		
			// Plugins
			queryDslPluginVersion = '1.0.7'
			dockerComposePluginVersion = '0.3.16'
		
		}
		
		

		
		// Be aware: I am not using allprojects because the main build.gradle should not create a jar file but it does :(
		project.subprojects {
			
			// *****************   COMMON REPOSITORIES FOR DEPENDENCIES   *****************
			repositories {
				mavenCentral()
				maven { url 'https://dl.bintray.com/palantir/releases' }
			}
		
	
		
			// *****************   COMMON PLUGINS   *****************
			buildscript {
				repositories {
					mavenCentral()
					maven { url 'https://plugins.gradle.org/m2/' }
				}
                // This is useless (it only works from the main build.gradle script)  Anyhow I am leaving here this configuration.
                dependencies {
                    classpath group: "info.solidsoft.gradle.pitest", name: "gradle-pitest-plugin", version: "1.1.4"
                }
			}
			apply plugin: 'java'
			apply plugin: 'idea'
			apply plugin: 'jacoco'
	        apply plugin: 'eclipse'
			apply plugin: 'maven-publish'
			apply plugin: 'info.solidsoft.pitest'

		
			targetCompatibility = 1.8
			sourceCompatibility = 1.8

			
			
			// *****************   PUBLISH TO REPOSITORY   *****************
			// Calls javadoc plugin and creates jar with the generated docs
			task("javadocJar", type: Jar) {
				from javadoc
				classifier 'javadoc'
			}
		
			// Calls java plugin and creates jar with the sources
			task("sourceJar", type: Jar) {
				from sourceSets.main.java
				classifier 'sources'
			}
		
			publishing {
				publications {
					mavenJava(MavenPublication) {
						// Publishes war or jar file depending on whether we are using the war plugin.
						if (plugins.withType(WarPlugin)) {
							from components.web
						} else {
							from components.java
						}
		
						// Publishes jar with sources
						artifact sourceJar {
							classifier 'sources'
						}
						// Publishes jar with javadoc
						artifact javadocJar {
							classifier 'javadoc'
						}
		
						// By default, Maven scope will be runtime. We want scope compile :/
						pom.withXml {
							asNode().dependencies.'*'.findAll() {
								it.scope.text() == 'runtime' && project.configurations.compile.allDependencies.find { dep ->
									dep.name == it.artifactId.text()
								}
							}.each() {
								it.scope*.value = 'compile'
							}
						}
					}
				}
				repositories {
					maven {
						credentials {
							username project.artifactory_username
							password project.artifactory_password
						}
		
						if(project.version.endsWith('-SNAPSHOT')) {
							url 'http://artifactory/artifactory/libs-snapshot-local'
						} else {
							url 'http://artifactory/artifactory/libs-release-local'
						}
					}
				}
			}
		

		
			// *****************   COMMON DEPENDENCIES   *****************
			dependencies {
				// 1/3 Required dependency for log4j 2 with slf4j: binding between log4j2 and slf4j
				compile("org.apache.logging.log4j:log4j-slf4j-impl:${slf4jVersion}")
				// 2/3 Required dependency for log4j 2 with slf4j: log4j 2 maven plugin (it is the log4j 2 implementation)
				compile("org.apache.logging.log4j:log4j-core:${log4jCoreVersion}")
				// 3/3 Required dependency for getting rid of commons logging. This is the BRIDGE (no binding) between Jakarta Commons Logging (used by Spring)
				// and whatever I am using for logging (in this case I am using log4j 2) See: http://www.slf4j.org/legacy.html
				// We need exclusions in every dependency using Jakarta Commons Logging (see Spring dependencies below)
				compile("org.slf4j:jcl-over-slf4j:${jclOverSlf4jVersion}")
		
		
				compile("org.springframework:spring-context:${springVersion}") {
					exclude group: 'commons-logging', module: 'commons-logging'
				}
				compile('javax.inject:javax.inject:1')
				compile('cglib:cglib:3.2.4')
		
		
				// Unit tests
				testCompile("junit:junit:${junitVersion}")
				testCompile("org.mockito:mockito-core:${mockitoVersion}")
			}
		
		
	
			// *****************   MANIFEST FILE   *****************
			jar {
				manifest {
					attributes('Implementation-Title': 'Spring JPA Persistence, gradle example',
						'Implementation-Version': theVersion,
						'Build-Time': new Date().format("yyyy-MM-dd'T'HH:mm:ssZ"),
						'Built-By': System.getProperty('user.name'),
						'Built-JDK': System.getProperty('java.version')
					)
				}
			}
		

		
			// *****************   UNIT TESTS, COMMON CONFIGURATION   *****************
			test {
			
				// explicitly include or exclude tests
				exclude '**/*IntegrationShould.class'
			
				testLogging {
					events "PASSED", "FAILED", "SKIPPED"
				}
			
				jacoco {
					append = false
					destinationFile = file("$buildDir/jacoco/jacoco.exec")
					classDumpFile = file("$buildDir/jacoco/classpathdumps")
				}
			}
		

		
			// *****************   INTEGRATION TESTS, COMMON CONFIGURATION   *****************
			// Using another directory for integration tests enable me to run all the unit tests quickly from my IDE (InteliJ or Eclipse)
			// If I had in the same directory both types of test when I want to run all the unit tests from the root of the src/test directory
			// I would be also running the integration tests, and they are slower :(
			
			// Integration tests will be located in this path: src/integTest
			sourceSets {
				integTest {
					compileClasspath += main.output + test.output
					runtimeClasspath += main.output + test.output
					output.classesDir = test.output.classesDir
					output.resourcesDir = test.output.resourcesDir
				}
			}
			
			configurations {
				integTestCompile.extendsFrom testCompile
				integTestRuntime.extendsFrom testRuntime
			}
			
			
			task("integTest", type: Test) {
				// dependsOn startApp
				// finalizedBy stopApp
				description = 'Runs integration tests';
				group = 'verification';
			
				testClassesDir = sourceSets.integTest.output.classesDir
				classpath = sourceSets.integTest.runtimeClasspath
				reports.junitXml.destination = "${buildDir}/test-results/test"
			
			
			
				// explicitly include or exclude tests
				filter {
					includeTestsMatching "*IntegrationShould"
					failOnNoMatchingTests = false
				}
			
				testLogging {
					events "PASSED", "FAILED", "SKIPPED"
				}
			
				jacoco {
					append = false
					destinationFile = file("$buildDir/jacoco/jacoco-it.exec")
					classDumpFile = file("$buildDir/jacoco/classpathIntegdumps")
				}
			
				// mustRunAfter test
			}
			
			tasks.check.dependsOn(integTest)
		
		
		
			// *****************   COVERAGE   *****************
			apply plugin: 'jacoco'
		
			jacoco {
				toolVersion = '0.7.6.201602180812'
				reportsDir = file("$buildDir/reports/jacoco")
			}
		
			jacocoTestReport {
				reports {
					xml.enabled false
					csv.enabled false
					html.destination "${buildDir}/reports/jacoco/"
				}
			}
		

		
			// *****************   JAVADOC   *****************
			javadoc {
				source = sourceSets.main.allJava
				classpath = configurations.compile
			}
		}	
	}
}
