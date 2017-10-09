package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

	private String eventType;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
	private LocalDateTime timestamp;
	private NotificationPayload payload;
	private String error;
	private String publisherId;
	private String messageId;
	
	@JsonProperty("event_type")
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public NotificationPayload getPayload() {
		return payload;
	}
	public void setPayload(NotificationPayload payload) {
		this.payload = payload;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@JsonProperty("publisher_id")
	public String getPublisherId() {
		return publisherId;
	}
	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	@JsonProperty("message_id")
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	@Override
	public String toString() {
		return "Notification [eventType=" + eventType + ", timestamp=" + timestamp + ", publisherId=" + publisherId
				 + ", messageId=" + messageId+ ", error=" + error + ", payload=" + payload + "]";
	}
	
}
