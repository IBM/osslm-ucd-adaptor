package com.accantosystems.stratoss.driver.ucd.model.heat;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Parameter {

	private String type;
	private String description;
	private String label;
	private String defaultValue;
	private boolean hidden;
	private List<Map<String, String>> constraints;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDefault() {
		return defaultValue;
	}
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public List<Map<String, String>> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Map<String, String>> constraints) {
		this.constraints = constraints;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"Parameter\", \"type\":\"%s\", \"description\":\"%s\", \"label\":\"%s\", \"default\":\"%s\", \"hidden\":\"%s\", \"constraints\":%s}",
				type, description, label, defaultValue, hidden, constraints);
	}
	
}
