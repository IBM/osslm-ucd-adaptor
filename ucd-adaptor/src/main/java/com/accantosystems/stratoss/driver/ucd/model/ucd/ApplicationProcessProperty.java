package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//  {
//    "id": "decc3570-17ae-4b46-b640-6a7180edafa9",
//    "name": "MyProperty",
//    "label": "MyProperty",
//    "pattern": "",
//    "type": "TEXT",
//    "value": "",
//    "required": false,
//    "description": "",
//    "placeholder": ""
//  }

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationProcessProperty {

	private String id;
	private String name;
	private String label;
	private String pattern;
	private String type;
	private String value;
	private boolean required;
	private String description;
	private String placeholder;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"ApplicationProcessProperty\", \"id\":\"%s\", \"name\":\"%s\", \"label\":\"%s\", \"pattern\":\"%s\", \"type\":\"%s\", \"value\":\"%s\", \"required\":\"%s\", \"description\":\"%s\", \"placeholder\":\"%s\"}",
				id, name, label, pattern, type, value, required, description, placeholder);
	}
	
}
