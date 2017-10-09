package com.accantosystems.stratoss.driver.ucd.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.client.UrbanCodeDeployClient;
import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;
import com.accantosystems.stratoss.driver.ucd.model.heat.HeatTemplate;
import com.accantosystems.stratoss.driver.ucd.model.heat.Resource;
import com.accantosystems.stratoss.driver.ucd.model.stratoss.OperationDescriptor;
import com.accantosystems.stratoss.driver.ucd.model.stratoss.PropertyDescriptor;
import com.accantosystems.stratoss.driver.ucd.model.stratoss.PropertyType;
import com.accantosystems.stratoss.driver.ucd.model.stratoss.ResourceDescriptor;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessDetails;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessInfo;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Blueprint;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ProcessActivity;
import com.accantosystems.stratoss.driver.ucd.utils.HeatTemplateUtils;
import com.accantosystems.stratoss.driver.ucd.utils.UCDUtils;
import com.accantosystems.stratoss.driver.ucd.web.rest.ResourceTypeNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

@Service
public class DefinitionService {
	
	private final UCDProperties ucdProperties;
	private final UrbanCodeDeployClient urbanCodeDeployClient;
	
	@Autowired
	public DefinitionService( UCDProperties ucdProperties, UrbanCodeDeployClient urbanCodeDeployClient ) {
		this.ucdProperties = ucdProperties;
		this.urbanCodeDeployClient = urbanCodeDeployClient;
	}

	public String getDefinition(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName, final String blueprintLocation) throws DriverExecutionException, ResourceTypeNotFoundException {
		Blueprint blueprint = urbanCodeDeployClient.getBlueprint(ucdEnvironment, blueprintName);
		HeatTemplate heatTemplate = HeatTemplateUtils.getHeatTemplateFromBlueprint(blueprint);
		
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setName( UCDUtils.createResourceTypeName(blueprintName) );
		resourceDescriptor.setDescription( heatTemplate.getDescription() );
		resourceDescriptor.setResourceManagerType( ucdProperties.getResourceManagerName() );
		heatTemplate.getParameters().entrySet().stream()
			.filter( entry -> entry.getKey() != null && !entry.getKey().startsWith(Constants.UCD_PARAMETER_PREFIX) )
			.forEach( entry -> {
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
				propertyDescriptor.setType( PropertyType.STRING ); // Only support Strings now, should convert #entry.getValue().getType()
				propertyDescriptor.setDescription( entry.getValue().getDescription() );
				propertyDescriptor.setDefault( entry.getValue().getDefault() );
				propertyDescriptor.setRequired( StringUtils.isEmpty(entry.getValue().getDefault()) );
				resourceDescriptor.getProperties().put( entry.getKey(), propertyDescriptor );
			});
		heatTemplate.getOutputs().entrySet().stream()
			.filter( entry -> !Constants.PARAM_BLUEPRINT_URL.equals(entry.getKey()) )
			.forEach( entry -> {
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
				propertyDescriptor.setType( PropertyType.STRING );
				propertyDescriptor.setReadOnly( true );
				propertyDescriptor.setDescription( entry.getValue().getDescription() );
				// Object (non-String) values here are internal UCD mapping attributes, which we don't want to expose
				if ( entry.getValue().getValue() instanceof String ) {
					propertyDescriptor.setDefault( entry.getValue().getValue().toString() );
				}
				resourceDescriptor.getProperties().put( entry.getKey(), propertyDescriptor );
			});
		
		Set<String> componentNames = new HashSet<>();
		
		heatTemplate.getResources().forEach( (name, resource) -> {
			// Get a list of the UCD Components used within the template
			if ( Blueprint.RESOURCE_TYPE_UCD_SOFTWARE_DEPLOY.equals(resource.getType()) ) {
				componentNames.add(name); // For now, we're only interesting in the name. For the definition, we really also need the version (typically LATEST)
			}
		});
		
		// Set default processes
		resourceDescriptor.getLifecycle().add( Constants.TRANSITION_NAME_INSTALL );
		resourceDescriptor.getLifecycle().add( Constants.TRANSITION_NAME_UNINSTALL );
		
		// Retrieve the application name (stored in the resource tree)
		Optional<Resource> resourceTree  = heatTemplate.getResources().values().stream().filter( resource -> Blueprint.RESOURCE_TYPE_UCD_RESOURCE_TREE.equals(resource.getType())).findFirst();
		if ( resourceTree.isPresent() ) {
			String applicationName = (String) resourceTree.get().getProperties().get("application");
			
			// Get all the processes for this application
			List<ApplicationProcessInfo> applicationProcesses = urbanCodeDeployClient.getProcessesForApplication(ucdEnvironment, applicationName);
			
			// For each process, retrieve the details (including process description) and then search the activities to find matching component names)
			applicationProcesses.stream().filter( process -> "applicationProcess".equals(process.getMetadataType()) ).forEach( process -> {
				ApplicationProcessDetails applicationProcessDetails = urbanCodeDeployClient.getApplicationProcessDetails(ucdEnvironment, process.getId(), process.getVersion());
				if ( applicationProcessDetails.getRootActivity() != null && findMatchingComponent(componentNames, applicationProcessDetails.getRootActivity().getChildren()) ) {
					if ( Constants.VALID_TRANSITION_NAMES.contains( process.getName() ) ) {
						resourceDescriptor.getLifecycle().add( process.getName() );
					} else {
						// If this process references one of the components used in the template, add it to the list of possible processes
						OperationDescriptor operationDescriptor = new OperationDescriptor();
						operationDescriptor.setDescription(process.getDescription());
						if ( applicationProcessDetails.getPropDefs() != null ) {
							applicationProcessDetails.getPropDefs().forEach( (property) -> {
								operationDescriptor.getProperties().put( property.getName(), new PropertyDescriptor(PropertyType.STRING, property.getDescription(), false, null, property.getValue(), StringUtils.isEmpty(property.getValue())) );
							});
						}
						resourceDescriptor.getOperations().put( process.getName(), operationDescriptor );
					}
				}
			});
		}
		
		try {
	        YAMLFactory yamlFactory = new YAMLFactory();
	        yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
	        yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);
	        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
	        ObjectMapper mapper = new ObjectMapper(yamlFactory);

			return mapper.writeValueAsString(resourceDescriptor);
		} catch (JsonProcessingException e) {
			throw new DriverExecutionException("Exception creating component package YAML", e);
		}
	}

	private boolean findMatchingComponent( final Set<String> componentNames, final List<ProcessActivity> activities ) {
		for (ProcessActivity processActivity : activities) {
			if ( processActivity.getComponentName() != null && componentNames.contains(processActivity.getComponentName()) ) {
				return true;
			} else if ( processActivity.getChildren() != null && !processActivity.getChildren().isEmpty() && findMatchingComponent( componentNames, processActivity.getChildren() ) ) {
				return true;
			}
		}
		return false;
	}
	
}
