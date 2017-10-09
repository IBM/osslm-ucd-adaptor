package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//{
//    "category": "networking",
//    "icon": "images/editor/Network_Internal.gif",
//    "id": "12345",
//    "name": "net-1"
//}

@JsonIgnoreProperties(ignoreUnknown=true)
public class Resource {

	private String id;
	private String name;
	private String category;
	private String icon;
	
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"Resource\", \"id\":\"%s\", \"name\":\"%s\", \"category\":\"%s\", \"icon\":\"%s\"}", id,
				name, category, icon);
	}
	
}
