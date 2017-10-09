package com.accantosystems.stratoss.driver.ucd.model.heat;

public class ParameterConstraint {

	private String allowed_pattern;
	private String description;
	
	public String getAllowed_pattern() {
		return allowed_pattern;
	}
	public void setAllowed_pattern(String allowed_pattern) {
		this.allowed_pattern = allowed_pattern;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"ParameterConstraint\", \"allowedPattern\":\"%s\", \"description\":\"%s\"}",
				allowed_pattern, description);
	}

}
