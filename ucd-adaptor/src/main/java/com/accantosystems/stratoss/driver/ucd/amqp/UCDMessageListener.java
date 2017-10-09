package com.accantosystems.stratoss.driver.ucd.amqp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.accantosystems.stratoss.driver.ucd.model.ucd.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UCDMessageListener implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(UCDMessageListener.class);
	
	@Override
	public void onMessage(Message message) {
		try {
        	ObjectMapper objectMapper = new ObjectMapper();
        	objectMapper.findAndRegisterModules();
        	Notification notification = objectMapper.readValue(message.getBody(), Notification.class);
            logger.info("Message received: {}", notification.toString());
		} catch (IOException e) {
			logger.error("Exception raised whilst processing AMQP message from UCD", e);
		}
	}

}
