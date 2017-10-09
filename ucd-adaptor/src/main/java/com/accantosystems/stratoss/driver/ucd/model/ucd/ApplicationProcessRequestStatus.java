package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

//	{
//	  "status": "CLOSED",
//	  "result": "SUCCEEDED"
//	}

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationProcessRequestStatus {

	private Status status;
	private Result result;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return String.format("{\"_class\":\"ApplicationProcessRequestStatus\", \"status\":\"%s\", \"result\":\"%s\"}",
				status, result);
	}

	public enum Status {
		CANCELING,
		CLOSED,
		COMPENSATING,
		EXECUTING,
		FAULTED,
		FAULTING,
		INITIALIZED,
		PENDING,
		UNINITIALIZED;
	}
	
	public enum Result {
		APPROVAL_REJECTED("APPROVAL REJECTED"),
		AWAITING_APPROVAL("AWAITING APPROVAL"),
		CANCELED("CANCELED"),
		COMPENSATED("COMPENSATED"),
		FAILED_TO_START("FAILED TO START"),
		FAULTED("FAULTED"),
		NONE("NONE"),
		SCHEDULED_FOR_FUTURE("SCHEDULED FOR FUTURE"),
		SUCCEEDED("SUCCEEDED"),
		UNINITIALIZED("UNINITIALIZED");
		
		private final String value;
		
		private Result(final String value) {
			this.value = value;
		}
		
		@JsonValue
		@Override
		public String toString() {
			return this.value;
		}
		
		@JsonCreator
		public static Result forValue( String value ) {
			for (Result result : Result.values()) {
				if ( result.toString().equalsIgnoreCase(value) ) {
					return result;
				}
			}
			return null;
		}
		
	}
	
}
