package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EnvironmentOutputParameter {

	private String key;
	private String value;
	private String description;
	
	@JsonProperty("output_key")
	public String getKey() {
		return key;
	}
	@JsonProperty("output_value")
	public String getValue() {
		return value;
	}
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"EnvironmentOutputParameter\", \"key\":\"%s\", \"value\":\"%s\", \"description\":\"%s\"}",
				key, value, description);
	}

}
