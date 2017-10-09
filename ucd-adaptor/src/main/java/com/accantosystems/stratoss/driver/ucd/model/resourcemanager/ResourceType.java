package com.accantosystems.stratoss.driver.ucd.model.resourcemanager;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(value=JsonInclude.Include.NON_EMPTY, content=JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResourceType {

	@ApiModelProperty(position=1, value="Name for the resource type", example="resource::Network::1.0")
	private String name;
	@ApiModelProperty(position=2, value="YAML descriptor for the resource type", example="[encoded YAML]")
	private String descriptor;
	@ApiModelProperty(position=3, value="Current state of this resource type", example="published")
	private ResourceTypeState state;
	@ApiModelProperty(position=4, required=false, value="Optional date and time this resource type was created", example="2017-05-01T12:00:00Z")
	private OffsetDateTime createdAt;
	@ApiModelProperty(position=5, required=false, value="Optional date and time this resource type was last modified", example="2017-05-01T12:00:00Z")
	private OffsetDateTime lastModifiedAt;

	public ResourceType() {
		super();
	}
	public ResourceType(String name, String descriptor, ResourceTypeState state, OffsetDateTime createdAt,
			OffsetDateTime lastModifiedAt) {
		super();
		this.name = name;
		this.descriptor = descriptor;
		this.state = state;
		this.createdAt = createdAt;
		this.lastModifiedAt = lastModifiedAt;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescriptor() {
		return descriptor;
	}
	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}
	public ResourceTypeState getState() {
		return state;
	}
	public void setState(ResourceTypeState state) {
		this.state = state;
	}
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public OffsetDateTime getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(OffsetDateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	
	public enum ResourceTypeState {
		UNPUBLISHED,
		PUBLISHED,
		DELETED
	}
	
}
