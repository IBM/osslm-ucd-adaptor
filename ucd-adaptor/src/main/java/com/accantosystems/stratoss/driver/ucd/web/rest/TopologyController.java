package com.accantosystems.stratoss.driver.ucd.web.rest;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.client.UrbanCodeDeployClient;
import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;
import com.accantosystems.stratoss.driver.ucd.model.heat.HeatTemplate;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.DeploymentLocation;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceInstance;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Blueprint;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Environment;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Environment.StackStatus;
import com.accantosystems.stratoss.driver.ucd.model.ucd.EnvironmentProperty;
import com.accantosystems.stratoss.driver.ucd.utils.HeatTemplateUtils;
import com.accantosystems.stratoss.driver.ucd.utils.UCDUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/resource-manager/topology")
public class TopologyController {
	
	private static final Logger logger = LoggerFactory.getLogger(TopologyController.class);

	private UCDProperties properties;
	private UrbanCodeDeployClient ucdClient;
	
	@Autowired
	public TopologyController(UCDProperties properties, UrbanCodeDeployClient ucdClient) {
		super();
		this.properties = properties;
		this.ucdClient = ucdClient;
	}
	
	@GetMapping("/deployment-locations")
	@ApiOperation(value="List Deployment Locations", notes="Returns a list of deployment locations managed by this Resource Manager")
	public List<DeploymentLocation> getDeploymentLocations() {
		logger.debug("Received request to list all deployment locations");
		return properties.getEnvironments().keySet()
			.stream()
			.map( environmentName -> new DeploymentLocation(environmentName, Constants.DEPLOYMENT_LOCATION_TYPE) )
			.collect(Collectors.toList());
	}
	
	@GetMapping("/deployment-locations/{name}")
	@ApiOperation(value="Get Deployment Location", notes="Returns information for the specified deployment location")
	public ResponseEntity<DeploymentLocation> getDeploymentLocation(@ApiParam(value="Unique name of the deployment location", required=true, defaultValue="admin@localstack") @PathVariable String name) {
		logger.debug(String.format("Received request for deployment location [%s]", name));
		if ( properties.getEnvironments().containsKey(name) ) {
			return ResponseEntity.ok(new DeploymentLocation(name, Constants.DEPLOYMENT_LOCATION_TYPE));
		}
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/deployment-locations/{name}/instances")
	@ApiOperation(value="Search for Resource Instances", notes="Searches for resource instances managed within the specified deployment location")
	public List<ResourceInstance> getInstances(@ApiParam(value="Unique name of the deployment location", required=true, defaultValue="admin@localstack") @PathVariable String name,
											   @ApiParam(value="Exact match of resource type", example="resource::OpenStackNetwork") @RequestParam("instanceType") Optional<String> instanceType,
											   @ApiParam("Partial match for resource instance name") @RequestParam("instanceName") Optional<String> instanceName) throws ResourceNotFoundException, UnsupportedResourceTypeException, DriverExecutionException {
		logger.debug(String.format("Received request to search for instance [%s] of type [%s] in deployment location [%s]", instanceName.orElse(null), instanceType.orElse(null), name));
		if ( Constants.NETWORK_TYPE.equalsIgnoreCase(instanceType.orElse(null)) ) {
			String networkId = ucdClient.getNetworkId(UCDUtils.getUCDEnvironment(properties, name), instanceName.orElse(null));
			if ( networkId != null ) {
				return Arrays.asList(new ResourceInstance(networkId, instanceName.get(), instanceType.get(), properties.getResourceManagerName(), name, null, null));
			}
		} else if ( Constants.IMAGE_TYPE.equalsIgnoreCase(instanceType.orElse(null)) ) {
			String imageId = ucdClient.getImageId(UCDUtils.getUCDEnvironment(properties, name), instanceName.orElse(null));
			if ( imageId != null ) {
				return Arrays.asList(new ResourceInstance(imageId, instanceName.get(), instanceType.get(), properties.getResourceManagerName(), name, null, null));
			}
		} else if ( Constants.FLAVOR_TYPE.equalsIgnoreCase(instanceType.orElse(null)) ) {
			String flavorId = ucdClient.getFlavorId(UCDUtils.getUCDEnvironment(properties, name), instanceName.orElse(null));
			if ( flavorId != null ) {
				return Arrays.asList(new ResourceInstance(flavorId, instanceName.get(), instanceType.get(), properties.getResourceManagerName(), name, null, null));
			}
		} else if ( Constants.KEYPAIR_TYPE.equalsIgnoreCase(instanceType.orElse(null)) ) {
			String keypairId = ucdClient.getKeypairId(UCDUtils.getUCDEnvironment(properties, name), instanceName.orElse(null));
			if ( keypairId != null ) {
				return Arrays.asList(new ResourceInstance(keypairId, instanceName.get(), instanceType.get(), properties.getResourceManagerName(), name, null, null));
			}
		} else {
			throw new UnsupportedResourceTypeException( String.format("Unable to search for instances of type [%s]", instanceType.orElse(null)) );
		}
		throw new ResourceNotFoundException( String.format("Unable to find resource instance with name [%s] of type [%s] in deployment location [%s]", instanceName.orElse(null), instanceType.orElse(null), name) );
	}
	
	@GetMapping("/instances/{id}")
	@ApiOperation(value="Get Resource Instance", notes="Returns information for the specified resource instance")
	public ResponseEntity<ResourceInstance> getInstance(@ApiParam(value="Unique id for the resource instance", required=true, example="c675e0bd-9c6c-43ca-84bf-2c061d439c6b") @PathVariable String id) throws DriverExecutionException, ResourceTypeNotFoundException, ResourceNotFoundException {
		logger.debug(String.format("Received request for resource instance [%s]", id));
		String[] environmentParts = id.split(Constants.RESOURCE_TYPE_DELIMITER);
		if ( environmentParts == null || environmentParts.length < 2 ) {
			return ResponseEntity.badRequest().header("X-Exception", String.format("Cannot find resource instance with incorrect resource type syntax [%s]", id)).build();
		}
		UrbanCodeDeployEnvironment ucdEnvironment = UCDUtils.getUCDEnvironment(properties, environmentParts[0]);
		// Get Environment from UCDP
		Environment environment = ucdClient.getEnvironment(ucdEnvironment, id);
		if ( StackStatus.DELETE_COMPLETE.toString().equals(environment.getStackStatus()) ) {
			return ResponseEntity.notFound().build();
		}
		ResourceInstance resourceInstance = new ResourceInstance(id, environment.getName(), UCDUtils.createResourceTypeName(environment.getBlueprintName()), properties.getResourceManagerName(), environment.getCloudProjectName(), environment.getCreationTime().toInstant().atOffset(ZoneOffset.UTC), null);
		environment.getParameters().entrySet().stream()
			.filter( entry -> entry.getKey() != null && !entry.getKey().startsWith(Constants.UCD_PARAMETER_PREFIX) )
			.forEach( entry -> resourceInstance.getProperties().put(entry.getKey(), entry.getValue()) );
		environment.getOutputs().stream()
			.filter( parameter -> !Constants.PARAM_BLUEPRINT_URL.equals(parameter.getKey()) )
			.forEach( parameter -> resourceInstance.getProperties().put(parameter.getKey(), parameter.getValue()) ); 
		// Get Properties from UCD
		Blueprint blueprint = ucdClient.getBlueprint(ucdEnvironment, environment.getBlueprintName());
		HeatTemplate heatTemplate = HeatTemplateUtils.getHeatTemplateFromBlueprint(blueprint);
		String applicationName = HeatTemplateUtils.getApplicationNameFromHeatTemplate(heatTemplate);
		String componentName = HeatTemplateUtils.getComponentNameFromHeatTemplate(heatTemplate);
		List<EnvironmentProperty> componentProperties = ucdClient.getComponentEnvironmentProperties(ucdEnvironment, environment.getName(), applicationName, componentName);
		componentProperties.forEach( property -> resourceInstance.getProperties().put(property.getName(), property.getValue()) );
		
		return ResponseEntity.ok(resourceInstance);
	}
	
}
