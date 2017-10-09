package com.accantosystems.stratoss.driver.ucd.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    	if ( logger.isDebugEnabled() ) {
    		traceRequest(request, body);
    	}
        ClientHttpResponse response = execution.execute(request, body);
        if ( logger.isDebugEnabled() ) {
        	traceResponse(response);
        }
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
    	StringBuilder requestDetails = new StringBuilder();
        requestDetails.append("Performing a " + request.getMethod() + " request to " + request.getURI() + LINE_SEPARATOR);
        requestDetails.append("Headers     : " + request.getHeaders() + LINE_SEPARATOR);
        requestDetails.append("Request body:" + LINE_SEPARATOR + new String(body, "UTF-8"));
        if ( logger.isDebugEnabled() ) {
        	logger.debug(requestDetails.toString());
        }
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        inputStringBuilder.append("Received response " + response.getStatusCode() + "/" + response.getStatusText() + LINE_SEPARATOR);
        inputStringBuilder.append("Headers      : " + response.getHeaders() + LINE_SEPARATOR);
        inputStringBuilder.append("Response body:" + LINE_SEPARATOR);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append(LINE_SEPARATOR);
            line = bufferedReader.readLine();
        }
        if ( logger.isDebugEnabled() ) {
        	logger.debug(inputStringBuilder.toString());
        }
    }

}
