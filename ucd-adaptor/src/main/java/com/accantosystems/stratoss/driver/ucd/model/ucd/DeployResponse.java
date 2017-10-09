package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DeployResponse {

	private String id;
	private List<Link> links = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void addLink(Link link) {
		this.links.add(link);
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"DeployResponse\", \"id\":\"%s\", \"links\":\"%s\"}", id, links);
	}
	
}
