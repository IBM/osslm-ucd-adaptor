package com.accantosystems.stratoss.driver.ucd.model.heat;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Resource {

	private String type;
	private Map<String, Object> properties;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"Resource\", \"type\":\"%s\"}", type);
	}
	
}
