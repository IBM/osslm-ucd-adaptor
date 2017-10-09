package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UniqueEnvironmentNameResponse {

	private String unique;

	public String getUnique() {
		return unique;
	}
	public void setUnique(String unique) {
		this.unique = unique;
	}

	@Override
	public String toString() {
		return String.format("{\"_class\":\"UniqueEnvironmentNameResponse\", \"unique\":\"%s\"}", unique);
	}
	
}
