package com.accantosystems.stratoss.driver.ucd.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessRequestStatus;
import com.accantosystems.stratoss.driver.ucd.utils.UCDUtils;

public class Constants {

	public static final String SUPPORTED_VERSION = "1.0";
	public static final String DEPLOYMENT_LOCATION_TYPE = "Cloud";
	
	public static final String RESOURCE_PREFIX = "resource";
	public static final String RESOURCE_TYPE_DELIMITER = "::";
	public static final String RESOURCE_TYPE_VERSION = "1.0";
	
	public static final String NETWORK = "UCD-Network";
	public static final String IMAGE = "UCD-Image";
	public static final String FLAVOR = "UCD-Flavor";
	public static final String KEYPAIR = "UCD-Keypair";
	
	public static final String NETWORK_TYPE = UCDUtils.createResourceTypeName(NETWORK);
	public static final String IMAGE_TYPE = UCDUtils.createResourceTypeName(IMAGE);
	public static final String FLAVOR_TYPE = UCDUtils.createResourceTypeName(FLAVOR);
	public static final String KEYPAIR_TYPE = UCDUtils.createResourceTypeName(KEYPAIR);
	
	public static final List<String> SUPPORTED_INTERNAL_TYPES = Collections.unmodifiableList(Arrays.asList(Constants.NETWORK_TYPE, Constants.IMAGE_TYPE, Constants.FLAVOR_TYPE, Constants.KEYPAIR_TYPE));

	public static final String SUCCESSFUL_INTEGRITY_RESULT_CODE = "0";
	public static final String PARAM_INTEGRITY_LATEST_RESULT_REASON = "Integrity_latest_result_reason";
	public static final String PARAM_INTEGRITY_LATEST_RESULT = "Integrity_latest_result";
	
	public static final String UCD_PARAMETER_PREFIX = "ucd_";
	public static final String PARAM_BLUEPRINT_URL = "blueprint_url";
	public static final String PARAM_DEPLOYMENT_LOCATION = "_deploymentLocation";
	public static final String PARAM_TRANSITION_NAME = "_transitionName";
	public static final String PARAM_ENVIRONMENT_NAME = "_environmentName";
	public static final String PARAM_APPLICATION_NAME = "_applicationName";
	public static final String PARAM_COMPONENT_NAME = "_componentName";
	
	public static final String ENVIRONMENT_CREATE_PREFIX = "envC-";
	public static final String ENVIRONMENT_DELETE_PREFIX = "envD-";
	
	// Valid transition names for assemblies/components
	public static final String TRANSITION_NAME_INSTALL = "Install";
	public static final String TRANSITION_NAME_CONFIGURE = "Configure";
	public static final String TRANSITION_NAME_START = "Start";
	public static final String TRANSITION_NAME_INTEGRITY = "Integrity";
	public static final String TRANSITION_NAME_STOP = "Stop";
	public static final String TRANSITION_NAME_UNINSTALL = "Uninstall";

	public static final List<String> VALID_TRANSITION_NAMES = Collections.unmodifiableList(Arrays.asList(TRANSITION_NAME_INSTALL, TRANSITION_NAME_CONFIGURE, TRANSITION_NAME_START, TRANSITION_NAME_INTEGRITY, TRANSITION_NAME_STOP, TRANSITION_NAME_UNINSTALL));
	public static final Set<ApplicationProcessRequestStatus.Status> VALID_APPLICATION_PROCESS_RUNNING_STATES = EnumSet.of(ApplicationProcessRequestStatus.Status.PENDING, ApplicationProcessRequestStatus.Status.UNINITIALIZED, ApplicationProcessRequestStatus.Status.INITIALIZED, ApplicationProcessRequestStatus.Status.EXECUTING);

	private Constants() {
		super();
	}
	
}
