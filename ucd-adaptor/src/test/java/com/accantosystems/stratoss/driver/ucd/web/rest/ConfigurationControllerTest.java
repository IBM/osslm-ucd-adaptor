package com.accantosystems.stratoss.driver.ucd.web.rest;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceManagerConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
public class ConfigurationControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationControllerTest.class);

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Test
	public void testGetConfiguration() {
		logger.debug("Received request for Resource Manager Configuration");
		ResourceManagerConfiguration resourceManagerConfiguration = testRestTemplate.getForObject("/api/resource-manager/configuration", ResourceManagerConfiguration.class);
		assertThat(resourceManagerConfiguration).isNotNull().isExactlyInstanceOf(ResourceManagerConfiguration.class);
		assertThat(resourceManagerConfiguration.getName()).isNotEmpty();
		assertThat(resourceManagerConfiguration.getVersion()).isNotEmpty();
		assertThat(resourceManagerConfiguration.getProperties()).isNotNull().isEmpty();
		assertThat(resourceManagerConfiguration.getSupportedApiVersions()).isNotEmpty().containsExactly("1.0");
		assertThat(resourceManagerConfiguration.getSupportedFeatures()).isNotNull().isEmpty();
	}

}
