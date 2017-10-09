package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

//{
//  "application": "Application name or ID",
//  "applicationProcess": "Application process name or ID",
//  "date": "Date and time to schedule the process for. (Optional) Supports unix timecodes or the format yyyy-mm-dd HH:mm",
//  "description": "Description for the request (Optional)",
//  "environment": "Environment name or ID",
//  "onlyChanged": "Specify false to force deployment of versions that are already in the inventory (Optional)",
//  "post-deploy-message": "The body of the PUT message. You can use the variable ${p:finalStatus}, which holds the state of the process. The possible states are:Success, Failure, Approval Rejected, Awaiting Approval, Running, Scheduled, Cancelled, and Unknown. (Optional)",
//  "post-deploy-put-url": "The URL that the post-deploy-message is PUT to. (Optional)",
//  "properties": {"Property name": "Property value (Optional)"},
//  "recurrencePattern": "To make a scheduled process recur, specify 'D' (daily), 'W' (weekly), or 'M' (monthly). (Optional)",
//  "snapshot": "Snapshot name or ID (Optional)",
//  "versions": [{
//    "component": "Component name or ID for the version, if you are using version name instead of ID.",
//    "version": "Version name or ID (Repeat as necessary. Not used with snapshots)"
//  }]
//}	

public class ApplicationProcessRequest {

	private String application;
	private String applicationProcess;
	private LocalDateTime date;
	private String description;
	private String environment;
	private boolean onlyChanged;
	private String postDeployMessage = "{\"status\": \"${p:finalStatus}\"}";
	private String postDeployPutUrl = "http://host:port/api/processes/{id}/statusUpdate";
	private final Map<String, String> properties = new HashMap<String, String>();
	private RecurrencePattern recurrencePattern;
	private String snapshot;
	private final List<ComponentVersion> versions = new ArrayList<ComponentVersion>();
	
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationProcess() {
		return applicationProcess;
	}

	public void setApplicationProcess(String applicationProcess) {
		this.applicationProcess = applicationProcess;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public boolean isOnlyChanged() {
		return onlyChanged;
	}

	public void setOnlyChanged(boolean onlyChanged) {
		this.onlyChanged = onlyChanged;
	}

	@JsonProperty("post-deploy-message")
	public String getPostDeployMessage() {
		return postDeployMessage;
	}

	public void setPostDeployMessage(String postDeployMessage) {
		this.postDeployMessage = postDeployMessage;
	}

	@JsonProperty("post-deploy-put-url")
	public String getPostDeployPutUrl() {
		return postDeployPutUrl;
	}

	public void setPostDeployPutUrl(String postDeployPutUrl) {
		this.postDeployPutUrl = postDeployPutUrl;
	}

	public RecurrencePattern getRecurrencePattern() {
		return recurrencePattern;
	}

	public void setRecurrencePattern(RecurrencePattern recurrencePattern) {
		this.recurrencePattern = recurrencePattern;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public List<ComponentVersion> getVersions() {
		return versions;
	}

	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"ApplicationProcessRequest\", \"application\":\"%s\", \"applicationProcess\":\"%s\", \"date\":\"%s\", \"description\":\"%s\", \"environment\":\"%s\", \"onlyChanged\":\"%s\", \"postDeployMessage\":\"%s\", \"postDeployPutUrl\":\"%s\", \"properties\":\"%s\", \"recurrencePattern\":\"%s\", \"snapshot\":\"%s\", \"versions\":\"%s\"}",
				application, applicationProcess, date, description, environment, onlyChanged, postDeployMessage,
				postDeployPutUrl, properties, recurrencePattern, snapshot, versions);
	}

	public class ComponentVersion {
		
		private final String component;
		private final String version;
		
		public ComponentVersion(final String component, final String version) {
			super();
			this.component = component;
			this.version = version;
		}
		
		public String getComponent() {
			return component;
		}
		public String getVersion() {
			return version;
		}
		
		@Override
		public String toString() {
			return String.format("{\"_class\":\"ComponentVersion\", \"component\":\"%s\", \"version\":\"%s\"}",
					component, version);
		}
	}
	
	public enum RecurrencePattern {
		DAILY("D"),
		WEEKLY("W"),
		MONTHLY("M");
		
		private final String value;
		
		private RecurrencePattern(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
}
