package com.accantosystems.stratoss.driver.ucd.web.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.client.UrbanCodeDeployClient;
import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceType;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceType.ResourceTypeState;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Blueprint;
import com.accantosystems.stratoss.driver.ucd.service.DefinitionService;
import com.accantosystems.stratoss.driver.ucd.utils.FileUtils;
import com.accantosystems.stratoss.driver.ucd.utils.UCDUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/resource-manager/types")
public class TypeController {
	
	private static final Logger logger = LoggerFactory.getLogger(TypeController.class);

	private final UCDProperties properties;
	private final UrbanCodeDeployClient ucdBlueprintClient;
	private final DefinitionService definitionService;
	
	@Autowired
	public TypeController(UCDProperties ucdProperties, UrbanCodeDeployClient ucdClient, DefinitionService definitionService) {
		super();
		this.properties = ucdProperties;
		this.ucdBlueprintClient = ucdClient;
		this.definitionService = definitionService;
	}
	
	@GetMapping
	@ApiOperation(value="List Resource Types", notes="Returns a list of all resource types managed by this Resource Manager")
	public List<ResourceType> getTypes() {
		logger.debug("Received request to list all types");
		// Get the static resources from the environment properties
		List<ResourceType> returnList = Constants.SUPPORTED_INTERNAL_TYPES.stream()
			.map( name -> new ResourceType(name, null, ResourceTypeState.PUBLISHED, null, null) )
			.collect(Collectors.toList());

		Map<String, Blueprint> blueprintMap = new HashMap<>();
		properties.getEnvironments().forEach( (name, ucdEnvironment) -> {
			List<Blueprint> blueprints = ucdBlueprintClient.getBlueprints(ucdEnvironment);
			blueprints.forEach( blueprint -> blueprintMap.put(blueprint.getName(), blueprint) );
		});
		
		blueprintMap.values().forEach( blueprint -> returnList.add( new ResourceType( UCDUtils.createResourceTypeName(blueprint.getName()), null, ResourceTypeState.PUBLISHED, null, null) ) );
		
		return returnList;
	}
	
	// Use this greedy regexp to get around Spring trying to do auto-content type negotiation
	@GetMapping("/{name:.+}")
	@ApiOperation(value="Get Resource Type", notes="Returns information about a specific resource type")
	public ResponseEntity<ResourceType> getType(@ApiParam(value="Name for the resource type", required=true, defaultValue="resource::UCD-Network::1.0") @PathVariable String name) throws ResourceTypeNotFoundException, UnsupportedResourceTypeException, DriverExecutionException, IOException {
		logger.debug(String.format("Received request for type [%s]", name));
		if ( name == null ) {
			throw new UnsupportedResourceTypeException("Resource type cannot be null"); // returns a 400
		}
		String[] resourceTypeParts = name.split(Constants.RESOURCE_TYPE_DELIMITER);
		if ( resourceTypeParts != null && resourceTypeParts.length >= 2 ) {
			String blueprintName = resourceTypeParts[1]; // Should be of the form resource::xxx::n.n
			
			if ( Constants.SUPPORTED_INTERNAL_TYPES.contains(name) ) {
				// Return internally supported resource type
				String descriptor = FileUtils.loadFileIntoString("types/" + blueprintName + ".yml");
				return ResponseEntity.ok(new ResourceType(blueprintName, descriptor, ResourceTypeState.PUBLISHED, null, null));
			}
			
			// Only problem here is we don't really know which UCD environment the resource type is in so loop through until its found
			for (Entry<String, UrbanCodeDeployEnvironment> ucdEnvironmentEntry : properties.getEnvironments().entrySet()) {
				Blueprint blueprintWithoutDescriptor = ucdBlueprintClient.findBlueprint(ucdEnvironmentEntry.getValue(), blueprintName);
				if ( blueprintWithoutDescriptor != null ) {
					String descriptor = definitionService.getDefinition(ucdEnvironmentEntry.getValue(), blueprintName, blueprintWithoutDescriptor.getLocation());
					return ResponseEntity.ok(new ResourceType(blueprintName, descriptor, ResourceTypeState.PUBLISHED, null, null));
				}
			}
		}
		
		throw new ResourceTypeNotFoundException(String.format("Cannot find resource type [%s]", name)); // returns a 404
	}

}
