package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
 * Example message format from UCD-Patterns v6.2.2 documentation
 * 
 * https://www.ibm.com/support/knowledgecenter/SS4GSP_6.2.2/com.ibm.edt.api.doc/topics/rest_bpid_deploy_put.html
 * 
 * {
		"additionalConfiguration": "A secondary configuration name (Optional)",
		"additionalConfigurationLocation": "A secondary configuration location (Optional)",
		"cloudProjectId": "Cloud project ID (Optional - default is the user's current cloud project)",
		"configuration": "Configuration name (Optional)",
		"configurationLocation": "Configuration location (Optional)",
		"environmentName": "Environment name",
		"parameters": {"name": "value (Optional - repeat for each parameter)"},
		"region": "Region name (Optional - default is the user's current region)",
		"teamMappings": "Array of UCD team ids (Optional)",
		"validate": "Boolean - whether or not to validate the data (Optional - default is false)"
	}
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeployRequest {

	private String cloudProjectId;
	private String cloudProjectLabel;
	private String environmentName;
	private final Map<String, String> parameters = new HashMap<>();
	private boolean validate = false;
	private String configuration;
	private String configurationLocation;
	private String additionalConfiguration;
	private String additionalConfigurationLocation;
	private String region; // unused
	private String teamMappings; // unused
	
	public String getCloudProjectId() {
		return cloudProjectId;
	}
	public void setCloudProjectId(String cloudProjectId) {
		this.cloudProjectId = cloudProjectId;
	}
	public String getCloudProjectLabel() {
		return cloudProjectLabel;
	}
	public void setCloudProjectLabel(String cloudProjectLabel) {
		this.cloudProjectLabel = cloudProjectLabel;
	}
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	public String getConfiguration() {
		return configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	public String getConfigurationLocation() {
		return configurationLocation;
	}
	public void setConfigurationLocation(String configurationLocation) {
		this.configurationLocation = configurationLocation;
	}
	public String getAdditionalConfiguration() {
		return additionalConfiguration;
	}
	public void setAdditionalConfiguration(String additionalConfiguration) {
		this.additionalConfiguration = additionalConfiguration;
	}
	public String getAdditionalConfigurationLocation() {
		return additionalConfigurationLocation;
	}
	public void setAdditionalConfigurationLocation(String additionalConfigurationLocation) {
		this.additionalConfigurationLocation = additionalConfigurationLocation;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getTeamMappings() {
		return teamMappings;
	}
	public void setTeamMappings(String teamMappings) {
		this.teamMappings = teamMappings;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"DeployRequest\", \"cloudProjectId\":\"%s\", \"cloudProjectLabel\":\"%s\", \"environmentName\":\"%s\", \"parameters.size()\":\"%s\", \"validate\":\"%s\", \"configuration\":\"%s\", \"configurationLocation\":\"%s\", \"additionalConfiguration\":\"%s\", \"additionalConfigurationLocation\":\"%s\", \"region\":\"%s\", \"teamMappings\":\"%s\"}",
				cloudProjectId, cloudProjectLabel, environmentName, parameters.size(), validate, configuration, configurationLocation,
				additionalConfiguration, additionalConfigurationLocation, region, teamMappings);
	}
	
}
