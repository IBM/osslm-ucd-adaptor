package com.accantosystems.stratoss.driver.ucd.utils;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.model.heat.HeatTemplate;
import com.accantosystems.stratoss.driver.ucd.model.heat.Resource;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Blueprint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class HeatTemplateUtils {

	private HeatTemplateUtils() {
		super();
	}

	public static HeatTemplate getHeatTemplateFromBlueprint( final Blueprint blueprint ) throws DriverExecutionException {
		// Extract and parse the Heat template (in YAML)
		YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
		yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);
		yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
		ObjectMapper mapper = new ObjectMapper(yamlFactory);
		try {
			return mapper.readValue(blueprint.getDocument(), HeatTemplate.class);
		} catch (IOException e) {
			throw new DriverExecutionException(String.format("Cannot parse Heat template from blueprint [%s]", blueprint.getName()), e);
		}
	}
	
	public static String getApplicationNameFromHeatTemplate(final HeatTemplate heatTemplate) throws DriverExecutionException {
		Optional<Resource> resourceTree  = heatTemplate.getResources().values()
				.stream()
				.filter( resource -> Blueprint.RESOURCE_TYPE_UCD_RESOURCE_TREE.equals(resource.getType()) )
				.findFirst();
		if ( resourceTree.isPresent() ) {
			return (String) resourceTree.get().getProperties().get("application");
		}
		return null;
	}
	
	public static String getComponentNameFromHeatTemplate(final HeatTemplate heatTemplate) throws DriverExecutionException {
		Optional<Entry<String, Resource>> softwareComponentEntry = heatTemplate.getResources().entrySet()
				.stream()
				.filter( resourceEntry -> Blueprint.RESOURCE_TYPE_UCD_SOFTWARE_DEPLOY.equals(resourceEntry.getValue().getType()) )
				.findFirst();
		if ( softwareComponentEntry.isPresent() ) {
			return softwareComponentEntry.get().getKey();
		}
		return null;
	}
	
}
