package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class OperationDescriptor {

    @JsonProperty("available-state")
    private String availableState;
    @JsonProperty("source-operation")
    private String sourceOperation;
    private String description;
    private final Map<String, PropertyDescriptor> properties = new TreeMap<>();

    public OperationDescriptor() {
        super();
    }
    public OperationDescriptor(String availableState, String sourceOperation, String description, Map<String, PropertyDescriptor> properties) {
        super();
        this.availableState = availableState;
        this.sourceOperation = sourceOperation;
        this.description = description;
        if ( properties != null ) {
        	this.properties.putAll(properties);
        }
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAvailableState() {
        return availableState;
    }
    public void setAvailableState(String availableState) {
        this.availableState = availableState;
    }
    public String getSourceOperation() {
		return sourceOperation;
	}
	public void setSourceOperation(String sourceOperation) {
		this.sourceOperation = sourceOperation;
	}
	public Map<String, PropertyDescriptor> getProperties() {
        return properties;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableState == null) ? 0 : availableState.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + properties.hashCode();
		result = prime * result + ((sourceOperation == null) ? 0 : sourceOperation.hashCode());
		return result;
	}
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperationDescriptor other = (OperationDescriptor) obj;
		if (availableState == null) {
			if (other.availableState != null)
				return false;
		} else if (!availableState.equals(other.availableState))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (!properties.equals(other.properties))
			return false;
		if (sourceOperation == null) {
			if (other.sourceOperation != null)
				return false;
		} else if (!sourceOperation.equals(other.sourceOperation))
			return false;
		return true;
	}
	
	@Override
    public String toString() {
        return String.format(
                "{\"_class\":\"OperationDescriptor\", \"availableState\":\"%s\", \"sourceOperation\":\"%s\", \"description\":\"%s\", \"properties\":%s}",
                availableState, sourceOperation, description, properties);
    }
}
