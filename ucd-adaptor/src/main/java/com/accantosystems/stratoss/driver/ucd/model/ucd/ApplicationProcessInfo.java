package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//	{
//	  "id": "b02d0960-e878-45b5-88d9-7461159f0e19",
//	  "name": "Deploy JPetStore",
//	  "description": "",
//	  "active": true,
//	  "inventoryManagementType": "AUTOMATIC",
//	  "offlineAgentHandling": "PRE_EXECUTION_CHECK",
//	  "versionCount": 2,
//	  "version": 2,
//	  "commit": 30,
//	  "path": "applications/c8a45972-da39-4450-87f0-21fae710e0f5/processes/b02d0960-e878-45b5-88d9-7461159f0e19"
//	}

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationProcessInfo {

	private String id;
	private String name;
	private String description;
	private boolean active;
	private String inventoryManagementType;
	private String offlineAgentHandling;
	private int versionCount;
	private int version;
	private int commit;
	private String path;
	private String metadataType;
	
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getInventoryManagementType() {
		return inventoryManagementType;
	}
	public void setInventoryManagementType(String inventoryManagementType) {
		this.inventoryManagementType = inventoryManagementType;
	}
	public String getOfflineAgentHandling() {
		return offlineAgentHandling;
	}
	public void setOfflineAgentHandling(String offlineAgentHandling) {
		this.offlineAgentHandling = offlineAgentHandling;
	}
	public int getVersionCount() {
		return versionCount;
	}
	public void setVersionCount(int versionCount) {
		this.versionCount = versionCount;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getCommit() {
		return commit;
	}
	public void setCommit(int commit) {
		this.commit = commit;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMetadataType() {
		return metadataType;
	}
	public void setMetadataType(String metadataType) {
		this.metadataType = metadataType;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"ApplicationProcessInfo\", \"id\":\"%s\", \"name\":\"%s\", \"description\":\"%s\", \"active\":\"%s\", \"inventoryManagementType\":\"%s\", \"offlineAgentHandling\":\"%s\", \"versionCount\":\"%s\", \"version\":\"%s\", \"commit\":\"%s\", \"path\":\"%s\", \"metadataType\":\"%s\"}",
				id, name, description, active, inventoryManagementType, offlineAgentHandling, versionCount, version,
				commit, path, metadataType);
	}
	
}
