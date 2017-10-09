package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//{
//    "config_file": "MyConfigOpenStack",
//    "errorMessage": "Key Pair (bad) does not exist.",
//    "errorType": "error",
//    "object": {
//        "parameters": {
//            "key_name": "bad"
//        }
//    },
//    "source": "configuration"
//}

@JsonIgnoreProperties(ignoreUnknown=true)
public class ValidateError {

	private String configFile;
	private String errorMessage;
	private String errorType;
	private String source;
	private ValidateErrorObject object;
	
	@JsonProperty("config_file")
	public String getConfigFile() {
		return configFile;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public String getErrorType() {
		return errorType;
	}
	public String getSource() {
		return source;
	}
	public ValidateErrorObject getObject() {
		return object;
	}

	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"ValidateError\", \"configFile\":\"%s\", \"errorMessage\":\"%s\", \"errorType\":\"%s\", \"source\":\"%s\", \"object\":\"%s\"}",
				configFile, errorMessage, errorType, source, object);
	}
	
}
