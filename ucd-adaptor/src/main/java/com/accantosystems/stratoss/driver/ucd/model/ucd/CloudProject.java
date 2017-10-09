package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//  {
//    "id": "a58fd508-26a9-45ed-820e-9ea2c5b48e35",
//    "name": "Demo",
//    "displayName": "Demo@localstack",
//    "description": "",
//    "cloudProviderId": "49a35c96-a02e-402b-9bc1-79656c9d907a",
//    "type": "AUTH_REALM",
//    "dateCreated": 1481208462039,
//    "readOnly": true,
//    "properties": []
//  }

@JsonIgnoreProperties(ignoreUnknown=true)
public class CloudProject {

	private String id;
	private String name;
	private String displayName;
	private String description;
	private String cloudProviderId;
	private String type;
	private String dateCreated;
	private boolean readOnly;
	private final List<Map<String, String>> properties = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getDescription() {
		return description;
	}
	public String getCloudProviderId() {
		return cloudProviderId;
	}
	public String getType() {
		return type;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public List<Map<String, String>> getProperties() {
		return properties;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"CloudProject\", \"id\":\"%s\", \"name\":\"%s\", \"displayName\":\"%s\", \"description\":\"%s\", \"cloudProviderId\":\"%s\", \"type\":\"%s\", \"dateCreated\":\"%s\", \"readOnly\":%s, \"properties\":%s}",
				id, name, displayName, description, cloudProviderId, type, dateCreated, readOnly, properties);
	}
	
}
