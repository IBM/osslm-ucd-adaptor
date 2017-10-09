package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class MetricDescriptor {

	private String type;
	@JsonProperty("publication-period")
	private String publicationPeriod;
	
	public MetricDescriptor() {
		super();
	}
	
	public MetricDescriptor(String type, String publicationPeriod) {
		super();
		this.type = type;
		this.publicationPeriod = publicationPeriod;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPublicationPeriod() {
		return publicationPeriod;
	}
	public void setPublicationPeriod(String publicationPeriod) {
		this.publicationPeriod = publicationPeriod;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((publicationPeriod == null) ? 0 : publicationPeriod.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricDescriptor other = (MetricDescriptor) obj;
		if (publicationPeriod == null) {
			if (other.publicationPeriod != null)
				return false;
		} else if (!publicationPeriod.equals(other.publicationPeriod))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("{\"_class\":\"MetricDescriptor\", \"type\":\"%s\", \"publicationPeriod\":\"%s\"}", type,
				publicationPeriod);
	}
	
}
