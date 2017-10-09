package com.accantosystems.stratoss.driver.ucd.model.heat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Output {

	private String description;
	private Object value;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"Output\", \"description\":\"%s\", \"value\":\"%s\"}", description, value);
	}
	
}
