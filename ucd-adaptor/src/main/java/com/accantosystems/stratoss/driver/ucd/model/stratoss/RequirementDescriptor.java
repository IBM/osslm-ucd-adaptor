package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class RequirementDescriptor extends CapabilityDescriptor {

	public RequirementDescriptor() {
		super();
	}
	public RequirementDescriptor(String type) {
		super(type);
	}

	@Override
	public String toString() {
		return String.format("{\"_class\":\"RequirementDescriptor\", \"type\":\"%s\"}", getType());
	}
	
}
