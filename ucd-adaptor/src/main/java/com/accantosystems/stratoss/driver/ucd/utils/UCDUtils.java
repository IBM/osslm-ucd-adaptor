package com.accantosystems.stratoss.driver.ucd.utils;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;

public class UCDUtils {

	public static UrbanCodeDeployEnvironment getUCDEnvironment(UCDProperties ucdProperties, String ucdEnvironmentName) throws DriverExecutionException {
		UrbanCodeDeployEnvironment ucdEnvironment = ucdProperties.getEnvironments().get(ucdEnvironmentName);
		if ( ucdEnvironment == null ) {
			throw new DriverExecutionException(String.format("Cannot find UrbanCode Deploy environment details. [%s]", ucdEnvironmentName));
		}
		return ucdEnvironment;
	}
	
	public static String createResourceTypeName(String resourceTypeName) {
		return Constants.RESOURCE_PREFIX + Constants.RESOURCE_TYPE_DELIMITER + resourceTypeName + Constants.RESOURCE_TYPE_DELIMITER + Constants.RESOURCE_TYPE_VERSION;
	}

	/**
	 * @param descriptorType
	 * @return
	 * @throws DriverExecutionException
	 */
	public static String getBlueprintName(final String descriptorType) throws DriverExecutionException {
		String[] descriptorNameParts = descriptorType.split(Constants.RESOURCE_TYPE_DELIMITER);
		if ( descriptorNameParts.length != 3 ) {
			throw new DriverExecutionException(String.format("Invalid resource type [%s]", descriptorType));
		}
		return descriptorNameParts[1];
	}

}
