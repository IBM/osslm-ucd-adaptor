package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//	{
//	  "document": "",
//	  "location": "/landscaper/orion/file/admin_75f400a5_6373_4475_ab40_6b0b1d8b4521-OrionContent/Internal-Team/loadbalancer-internal.yaml",
//	  "id": "loadbalancer-internal",
//	  "name": "loadbalancer-internal",
//	  "version": "1.0",
//	  "url": "https://10.0.9.38:8443/landscaper/view/projects?open=loadbalancer-internal"
//	}

//  {
//    "id": "loadbalancer-internal21",
//    "type": "blueprint",
//    "application": "vod_server",
//    "location": "/landscaper/orion/file/admin_75f400a5_6373_4475_ab40_6b0b1d8b4521-OrionContent/Internal-Team/loadbalancer-internal21.yaml",
//    "name": "loadbalancer-internal21",
//    "path": "loadbalancer-internal21.yaml",
//    "url": "https://10.0.9.38:8443/landscaper/view/projects?open=loadbalancer-internal21",
//    "category": "blueprint",
//    "icon": "images/editor/Blueprint_icon.png",
//    "version": "1.0",
//    "projectName": "Internal-Team"
//  }

@JsonIgnoreProperties(ignoreUnknown = true)
public class Blueprint {

	public static final String RESOURCE_TYPE_UCD_RESOURCE_TREE = "IBM::UrbanCode::ResourceTree";
	public static final String RESOURCE_TYPE_UCD_SOFTWARE_DEPLOY = "IBM::UrbanCode::SoftwareDeploy::UCD";
	public static final String RESOURCE_TYPE_OS_NEUTRON_PORT = "OS::Neutron::Port";
	public static final String RESOURCE_TYPE_OS_NEUTRON_NET = "OS::Neutron::Net";
	public static final String RESOURCE_TYPE_OS_NOVA_SERVER = "OS::Nova::Server";
	
	private String id;
	private String name;
	private String version;
	private String location;
	private String url;
	private String document;
	private String application;
	private String type;
	private String category;
	private String projectName;
	
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"Blueprint\", \"id\":\"%s\", \"name\":\"%s\", \"version\":\"%s\", \"location\":\"%s\", \"url\":\"%s\", \"application\":\"%s\", \"type\":\"%s\", \"category\":\"%s\", \"projectName\":\"%s\"}",
				id, name, version, location, url, application, type, category, projectName);
	}
	
}
