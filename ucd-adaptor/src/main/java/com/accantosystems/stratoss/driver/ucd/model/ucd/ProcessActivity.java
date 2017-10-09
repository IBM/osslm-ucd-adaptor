package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.List;

public class ProcessActivity {

	private String id;
	private String name;
	private String type;
	private String componentName;
	private List<ProcessActivity> children;
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public List<ProcessActivity> getChildren() {
		return children;
	}
	public void setChildren(List<ProcessActivity> children) {
		this.children = children;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"ProcessActivity\", \"id\":\"%s\", \"name\":\"%s\", \"type\":\"%s\", \"componentName\":\"%s\", \"children\":%s}",
				id, name, type, componentName, children);
	}
	
}
