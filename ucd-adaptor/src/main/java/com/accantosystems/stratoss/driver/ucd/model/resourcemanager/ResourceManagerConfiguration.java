package com.accantosystems.stratoss.driver.ucd.model.resourcemanager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(value=JsonInclude.Include.NON_EMPTY, content=JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResourceManagerConfiguration {

	@ApiModelProperty(position=1, example="default-rm::dev", required=false, value="Name for the Resource Manager")
	private String name;
	@ApiModelProperty(position=2, example="1.0.0", required=false, value="Version identifier for the Resource Manager (for display purposes only)")
	private String version;
	@ApiModelProperty(position=3, required=false, value="Array of supported API versions for this Resource Manager")
	private final Set<String> supportedApiVersions = new HashSet<>();
	@ApiModelProperty(position=4, required=false, value="Map of supported features for this Resource Manager")
	private final Map<String, String> supportedFeatures = new TreeMap<>();
	@ApiModelProperty(position=5, required=false, value="Map of properties for the configuration of this Resource Manager")
	private final Map<String, String> properties = new TreeMap<>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Set<String> getSupportedApiVersions() {
		return supportedApiVersions;
	}
	public Map<String, String> getSupportedFeatures() {
		return supportedFeatures;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	
}
