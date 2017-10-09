package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationPayload {

	private String tenantId;
	private String userId;
	private String stackIdentity;
	private String stackName;
	private String state;
	private String stateReason;
	private OffsetDateTime createAt;
	
	@JsonProperty("tenant_id")
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	@JsonProperty("user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@JsonProperty("stack_identity")
	public String getStackIdentity() {
		return stackIdentity;
	}
	public void setStackIdentity(String stackIdentity) {
		this.stackIdentity = stackIdentity;
	}
	@JsonProperty("stack_name")
	public String getStackName() {
		return stackName;
	}
	public void setStackName(String stackName) {
		this.stackName = stackName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@JsonProperty("state_reason")
	public String getStateReason() {
		return stateReason;
	}
	public void setStateReason(String stateReason) {
		this.stateReason = stateReason;
	}
	@JsonProperty("create_at")
	public OffsetDateTime getCreateAt() {
		return createAt;
	}
	public void setCreateAt(OffsetDateTime createAt) {
		this.createAt = createAt;
	}
	
	@Override
	public String toString() {
		return "NotificationPayload [tenantId=" + tenantId + ", userId=" + userId + ", stackIdentity=" + stackIdentity
				+ ", stackName=" + stackName + ", state=" + state + ", stateReason=" + stateReason + ", createAt="
				+ createAt + "]";
	}
	
}
