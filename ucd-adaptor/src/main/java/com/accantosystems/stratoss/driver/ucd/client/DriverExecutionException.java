package com.accantosystems.stratoss.driver.ucd.client;

public class DriverExecutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public DriverExecutionException() {
	}

	public DriverExecutionException(String message) {
		super(message);
	}

	public DriverExecutionException(Throwable cause) {
		super(cause);
	}

	public DriverExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DriverExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
