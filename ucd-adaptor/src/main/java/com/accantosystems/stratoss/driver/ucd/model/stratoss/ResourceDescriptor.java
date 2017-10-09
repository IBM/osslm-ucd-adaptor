package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResourceDescriptor {
	
	private String name;
	private String description;
    @JsonProperty("resource-manager-type")
    private String resourceManagerType;
    private final Map<String, PropertyDescriptor> properties = new TreeMap<>();
    private final Map<String, OperationDescriptor> operations = new TreeMap<>();
    private final Map<String, RequirementDescriptor> requirements = new TreeMap<>();
    private final Map<String, CapabilityDescriptor> capabilities = new TreeMap<>();
	@JsonDeserialize(as=TreeSet.class)
    private final Set<String> lifecycle = new TreeSet<>();
	private final Map<String, MetricDescriptor> metrics = new TreeMap<>();
	private final Map<String, PolicyDescriptor> policies = new TreeMap<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResourceManagerType() {
		return resourceManagerType;
	}
	public void setResourceManagerType(String resourceManagerType) {
		this.resourceManagerType = resourceManagerType;
	}
	public Map<String, PropertyDescriptor> getProperties() {
		return properties;
	}
	public Map<String, OperationDescriptor> getOperations() {
		return operations;
	}
	public Map<String, RequirementDescriptor> getRequirements() {
		return requirements;
	}
	public Map<String, CapabilityDescriptor> getCapabilities() {
		return capabilities;
	}
	public Set<String> getLifecycle() {
		return lifecycle;
	}
	public Map<String, MetricDescriptor> getMetrics() {
		return metrics;
	}
	public Map<String, PolicyDescriptor> getPolicies() {
		return policies;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"ResourceDescriptor\", \"name\":\"%s\", \"description\":\"%s\", \"resourceManagerType\":\"%s\", \"properties\":%s, \"operations\":%s, \"requirements\":%s, \"capabilities\":%s, \"lifecycle\":%s, \"metrics\":%s, \"policies\":%s}",
				name, description, resourceManagerType, properties, operations, requirements, capabilities, lifecycle, metrics, policies);
	}

}
