package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//	{
//	  "requestId": "f7e7b00d-8ea6-4a95-ad74-0ff853125232"
//	}

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationProcessResponse {

	private String requestId;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return String.format("{\"_class\":\"ApplicationProcessResponse\", \"requestId\":\"%s\"}", requestId);
	}
	
}
