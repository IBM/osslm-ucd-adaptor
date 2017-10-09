package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown=true)
public class PropertyDescriptor {

	// default: string
	private PropertyType type;
	// not mandatory
	private String description;
	// default false
	@JsonProperty("read-only")
	private boolean readOnly;
	// default: false
	private boolean required;
	private String value;
	@JsonProperty("default")
	private String defaultValue;
	
	public PropertyDescriptor() {
		super();
	}

	public PropertyDescriptor(String value) {
		super();
		this.value = value;
	}

	public PropertyDescriptor(PropertyType type, String description, boolean readOnly, String value, String defaultValue, boolean required) {
		super();
		this.type = type;
		this.description = description;
		this.readOnly = readOnly;
		this.value = value;
		this.defaultValue = defaultValue;
		this.required = required;
	}

	public PropertyType getType() {
		return type;
	}
	public void setType(PropertyType type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDefault() {
		return defaultValue;
	}
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (readOnly ? 1231 : 1237);
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PropertyDescriptor other = (PropertyDescriptor) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (readOnly != other.readOnly)
			return false;
		if (required != other.required)
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"PropertyDescriptor\", \"type\":\"%s\", \"description\":\"%s\", \"readOnly\":%s, \"value\":\"%s\", \"default\":\"%s\", \"required\":\"%s\"}",
				type, description, readOnly, value, defaultValue, required);
	}
	
}
