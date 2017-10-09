package com.accantosystems.stratoss.driver.ucd.model.resourcemanager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(value=JsonInclude.Include.NON_EMPTY, content=JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class DeploymentLocation {

	@ApiModelProperty(position=1, example="admin@localstack", value="Name of the deployment location")
	private String name;
	@ApiModelProperty(position=2, example="Cloud", value="Type identifier for the deployment location")
	private String type;

	public DeploymentLocation() {
		super();
	}
	public DeploymentLocation(String name, String type) {
		super();
		this.name = name;
		this.type = type;
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
	
}
