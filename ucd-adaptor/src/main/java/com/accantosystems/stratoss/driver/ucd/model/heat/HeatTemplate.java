package com.accantosystems.stratoss.driver.ucd.model.heat;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class HeatTemplate {

	private String heat_template_version;
	private String description;
	private Map<String, Parameter> parameters;
	private List<Map<String, Object>> parameter_groups;
	private Map<String, Resource> resources;
	private Map<String, Output> outputs;
	
	public String getHeat_template_version() {
		return heat_template_version;
	}
	public void setHeat_template_version(String heat_template_version) {
		this.heat_template_version = heat_template_version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Parameter> parameters) {
		this.parameters = parameters;
	}
	public List<Map<String, Object>> getParameter_groups() {
		return parameter_groups;
	}
	public void setParameter_groups(List<Map<String, Object>> parameter_groups) {
		this.parameter_groups = parameter_groups;
	}
	public Map<String, Resource> getResources() {
		return resources;
	}
	public void setResources(Map<String, Resource> resources) {
		this.resources = resources;
	}
	public Map<String, Output> getOutputs() {
		return outputs;
	}
	public void setOutputs(Map<String, Output> outputs) {
		this.outputs = outputs;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"HeatTemplate\", \"heat_template_version\":\"%s\", \"description\":\"%s\", \"parameters\":%s, \"resources\":%s, \"outputs\":%s}",
				heat_template_version, description, parameters, resources, outputs);
	}
	
}
