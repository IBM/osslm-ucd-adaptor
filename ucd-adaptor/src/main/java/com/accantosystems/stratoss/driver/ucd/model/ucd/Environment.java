package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//	{
//	  "id": "bbc98d07-5476-4fc5-9a4c-9324aa8a2c47",
//	  "name": "loadbalancer",
//	  "blueprintName": "loadbalancer",
//	  "blueprintUrl": "https://10.0.9.38:8443/landscaper/view/projects?open=admin_67f1f92c_d428_428e_9cdb_41513499ada0-OrionContent/Internal-Team/loadbalancer.yaml",
//	  "stack_status": "CREATE_COMPLETE",
//	  "stack_status_reason": "Stack CREATE completed successfully",
//	  "creation_time": "2016-12-07T09:54:02Z",
//	  "owner": "admin",
//	  "created_by": "admin",
//	  "regionName": "RegionOne",
//	  "cloudProjectName": "admin@localstack",
//	  "parameters": {
//	    "network-id__for__admin_net": "c0556a3b-fef3-43ae-bfd1-a9a45cdea106",
//	    "OS::project_id": "7002d3f993f14cff9de225bf7baa2610",
//	    "ucd_user": "admin",
//	    "ucd_server_url": "https://10.0.9.30:8443",
//	    "availability_zone": "nova",
//	    "OS::stack_name": "loadbalancer",
//	    "key_name": "martin",
//	    "ucd_password": "******",
//	    "OS::stack_id": "bbc98d07-5476-4fc5-9a4c-9324aa8a2c47",
//	    "OS::region": "RegionOne",
//	    "ucd_relay_url": "None",
//	    "IBM::UCD::USER": "admin",
//	    "public_network_id": "6a557676-969b-4fa7-8ce5-231c845a3e0d",
//	    "IBM::Cloud::Type": "AUTH_REALM",
//	    "flavor": "m1.small",
//	    "IBM::Cloud": "5582ff0b-f878-4513-b559-73c2429e524e"
//	  },
//	  "outputs": [
//	    {
//	      "output_value": "https://10.0.9.38:8443/landscaper/view/projects?open=admin_67f1f92c_d428_428e_9cdb_41513499ada0-OrionContent/Internal-Team/loadbalancer.yml",
//	      "description": "Blueprint Origin URL",
//	      "output_key": "blueprint_url"
//	    }
//	  ]
//	}

@JsonIgnoreProperties(ignoreUnknown=true)
public class Environment {

	private String id;
	private String name;
	private String blueprintName;
	private String blueprintUrl;
	private String stackStatus;
	private String stackStatusReason;
	private Date creationTime;
	private String owner;
	private String createdBy;
	private String regionName;
	private String cloudProjectName;
	private final Map<String, String> parameters = new HashMap<>();
	private final List<EnvironmentOutputParameter> outputs = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getBlueprintName() {
		return blueprintName;
	}
	public String getBlueprintUrl() {
		return blueprintUrl;
	}
	@JsonProperty("stack_status")
	public String getStackStatus() {
		return stackStatus;
	}
	@JsonProperty("stack_status_reason")
	public String getStackStatusReason() {
		return stackStatusReason;
	}
	@JsonProperty("creation_time")
	public Date getCreationTime() {
		return creationTime;
	}
	public String getOwner() {
		return owner;
	}
	@JsonProperty("created_by")
	public String getCreatedBy() {
		return createdBy;
	}
	public String getRegionName() {
		return regionName;
	}
	public String getCloudProjectName() {
		return cloudProjectName;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public List<EnvironmentOutputParameter> getOutputs() {
		return outputs;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"Environment\", \"id\":\"%s\", \"name\":\"%s\", \"blueprintName\":\"%s\", \"blueprintUrl\":\"%s\", \"stackStatus\":\"%s\", \"stackStatusReason\":\"%s\", \"creationTime\":\"%s\", \"owner\":\"%s\", \"createdBy\":\"%s\", \"regionName\":\"%s\", \"cloudProjectName\":\"%s\", \"parameters\":\"%s\", \"outputs\":\"%s\"}",
				id, name, blueprintName, blueprintUrl, stackStatus, stackStatusReason, creationTime, owner, createdBy,
				regionName, cloudProjectName, parameters, outputs);
	}
	
	public enum StackStatus {
		CREATE_IN_PROGRESS, CREATE_COMPLETE, DELETE_IN_PROGRESS, DELETE_COMPLETE;
	}
	
}
