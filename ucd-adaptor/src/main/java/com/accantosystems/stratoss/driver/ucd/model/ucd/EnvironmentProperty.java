package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// {
//   "id": "167fa275-bb95-460b-97b2-e27e713a1400",
//   "name": "tomcat.start",
//   "value": "/opt/apache-tomcat-6.0.37/bin/startup.sh",
//   "description": "",
//   "secure": false
// }

@JsonIgnoreProperties(ignoreUnknown=true)
public class EnvironmentProperty {

	private String id;
	private String name;
	private String value;
	private String description;
	private boolean secure;
	
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isSecure() {
		return secure;
	}
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"EnvironmentProperty\", \"id\":\"%s\", \"name\":\"%s\", \"value\":\"%s\", \"description\":\"%s\", \"secure\":\"%s\"}",
				id, name, value, description, secure);
	}
	
}
