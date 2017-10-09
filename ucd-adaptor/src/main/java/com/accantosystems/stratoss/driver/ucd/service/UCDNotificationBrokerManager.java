package com.accantosystems.stratoss.driver.ucd.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.accantosystems.stratoss.driver.ucd.amqp.UCDMessageListener;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;

@Service
public class UCDNotificationBrokerManager {
	
	private static final Logger logger = LoggerFactory.getLogger(UCDNotificationBrokerManager.class);
	
	private final UCDProperties ucdProperties;
	private final Map<String, SimpleMessageListenerContainer> containers = new HashMap<>();
	
	@Autowired
	public UCDNotificationBrokerManager(final UCDProperties ucdProperties) {
		super();
		this.ucdProperties = ucdProperties;
	}

	@PostConstruct
	public void init() {
		logger.info("Creating UCD notification listeners...");
		
		for (Entry<String, UrbanCodeDeployEnvironment> environmentEntry : ucdProperties.getEnvironments().entrySet()) {
			if ( environmentEntry.getValue().getNotifications().getHostname() != null ) {
				logger.info("Creating listener for environment [{}]", environmentEntry.getKey());
				
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				CachingConnectionFactory connectionFactory = new CachingConnectionFactory( environmentEntry.getValue().getNotifications().getHostname() );
				if ( !StringUtils.isEmpty(environmentEntry.getValue().getNotifications().getUsername()) ) {
					connectionFactory.setUsername( environmentEntry.getValue().getNotifications().getUsername() );
				}
				if ( !StringUtils.isEmpty(environmentEntry.getValue().getNotifications().getPassword()) ) {
					connectionFactory.setPassword( environmentEntry.getValue().getNotifications().getPassword() );
				}
		        container.setConnectionFactory(connectionFactory);
		        container.setQueueNames( "notifications.info", "notifications.error" );
		        container.setMessageListener( new UCDMessageListener() );
		        
		        try {
		        	// TODO Should we have a retry loop here?
					container.start();
				} catch (Exception e) {
					logger.warn("Exception raised trying to initialise connection to UCD notification broker", e);
				}
		        
		        containers.put(environmentEntry.getKey(), container);
			} else {
				logger.info("No notification broker configured for environment [{}]", environmentEntry.getKey());
			}
		}
	}
	
	@PreDestroy
	public void destroy() {
		for (Entry<String, SimpleMessageListenerContainer> containerEntry : containers.entrySet()) {
			logger.info("Stopping container [{}]", containerEntry.getKey());
			containerEntry.getValue().stop();
		}
	}
	
}
