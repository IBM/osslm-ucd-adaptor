package com.accantosystems.stratoss.driver.ucd.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceInstance;

public class RPCResponse {

	private final ResourceInstance resource;
	private final ResponseStatus status;
	private final boolean idempotent;
	private final List<String> messages = new ArrayList<>();
	private final Map<String, Object> context = new HashMap<>();
	
	public RPCResponse(final ResourceInstance resource, final ResponseStatus status, final boolean isIdempotent) {
		super();
		this.status = status;
		this.idempotent = isIdempotent;
		this.resource = resource;
	}
	public RPCResponse(final ResponseStatus status, final boolean isIdempotent) {
		this(null, status, isIdempotent);
	}
	public RPCResponse(final ResponseStatus status, final boolean isIdempotent, final List<String> messages) {
		this(null, status, isIdempotent);
		this.messages.addAll(messages);
	}
	public RPCResponse(final ResponseStatus status, final boolean isIdempotent, final String... messages) {
		this(null, status, isIdempotent);
		for (String message : messages) {
			this.messages.add(message);
		}
	}
	public RPCResponse(final ResourceInstance resource, final ResponseStatus status, final boolean isIdempotent, final Map<String, Object> context) {
		this(resource, status, isIdempotent);
		this.context.putAll(context);
	}
	public RPCResponse(final ResourceInstance resource, final ResponseStatus status, final boolean isIdempotent, final List<String> messages, final Map<String, Object> context) {
		this(resource, status, isIdempotent, context);
		this.messages.addAll(messages);
	}

	public ResourceInstance getResource() {
		return resource;
	}
	public ResponseStatus getStatus() {
		return status;
	}
	public boolean isIdempotent() {
		return idempotent;
	}
	public List<String> getMessages() {
		return messages;
	}
	public Map<String, Object> getContext() {
		return context;
	}
	
	@Override
	public String toString() {
		return "RPCResponse [resource=" + resource + "status=" + status + ", messages=" + messages + ", context=" + context + "]";
	}

	public enum ResponseStatus {
		SUCCESS, SUCCESS_WITH_WARNINGS, FAILED, BAD_REQUEST;
	}
	
}
