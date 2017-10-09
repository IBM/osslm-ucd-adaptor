package com.accantosystems.stratoss.driver.ucd.client;

public class MissingParametersException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingParametersException() {
	}

	public MissingParametersException(String message) {
		super(message);
	}

	public MissingParametersException(Throwable cause) {
		super(cause);
	}

	public MissingParametersException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingParametersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
