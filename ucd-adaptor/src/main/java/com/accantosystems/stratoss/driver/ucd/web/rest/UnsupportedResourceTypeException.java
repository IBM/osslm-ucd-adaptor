package com.accantosystems.stratoss.driver.ucd.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedResourceTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedResourceTypeException() {
	}

	public UnsupportedResourceTypeException(String arg0) {
		super(arg0);
	}

	public UnsupportedResourceTypeException(Throwable arg0) {
		super(arg0);
	}

	public UnsupportedResourceTypeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnsupportedResourceTypeException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
