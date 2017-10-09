package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyType {

	STRING("string"),
	INTEGER("integer"),
	BOOLEAN("boolean"),
	UUID("uuid"),
	PASSWORD("password");
	
	private final String value;
	
	private PropertyType(final String value) {
		this.value = value;
	}
	
	@JsonValue
	@Override
	public String toString() {
		return this.value;
	}
	
	@JsonCreator
    public static PropertyType fromJson(String type) {
		for (PropertyType propertyType : PropertyType.values()) {
			if ( propertyType.toString().equalsIgnoreCase(type) ) {
				return propertyType;
			}
		}
        return null;
    }
}
