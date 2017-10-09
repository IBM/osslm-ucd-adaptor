package com.accantosystems.stratoss.driver.ucd.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceManagerConfiguration;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/resource-manager/configuration")
public class ConfigurationController {

	@Value("${info.app.version:1.0}")
	private String appVersion;
	
	private UCDProperties properties;
	
	@Autowired
	public ConfigurationController(UCDProperties properties) {
		super();
		this.properties = properties;
	}
	
	@GetMapping
	@ApiOperation(value="Get Resource Manager Configuration", notes="Returns high-level information about the configuration of this Resource Manager")
	public ResponseEntity<ResourceManagerConfiguration> getConfiguration() {
		ResourceManagerConfiguration configuration = new ResourceManagerConfiguration();
		configuration.setName( properties.getResourceManagerName() );
		configuration.setVersion( appVersion );
		configuration.getSupportedApiVersions().add( Constants.SUPPORTED_VERSION );
		return ResponseEntity.ok(configuration);
	}
	
}
