package com.accantosystems.stratoss.driver.ucd.model.ucd;

public class Component {

	private String id;
	private String name;
	private String description;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"Component\", \"id\":\"%s\", \"name\":\"%s\", \"description\":\"%s\"}", id,
				name, description);
	}
	
}
