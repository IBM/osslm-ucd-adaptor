package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.HashMap;
import java.util.Map;

public class ValidateErrorObject {

	private final Map<String, Object> parameters = new HashMap<>();
	private final Map<String, Object> resources = new HashMap<>();

	public Map<String, Object> getParameters() {
		return parameters;
	}
	public Map<String, Object> getResources() {
		return resources;
	}

	@Override
	public String toString() {
		return String.format("{\"_class\":\"ValidateErrorObject\", \"parameters\":%s, \"resources\":%s}", parameters, resources);
	}
	
}
