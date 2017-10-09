package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Link {

	private String href;
	private String rel;
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"Link\", \"href\":\"%s\", \"rel\":\"%s\"}", href, rel);
	}
	
}
