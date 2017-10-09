package com.accantosystems.stratoss.driver.ucd.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.LoggingRequestInterceptor;
import com.accantosystems.stratoss.driver.ucd.config.SelfSignedCertificateClientHttpRequestFactory;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment.UrbanCodeDeployServer;
import com.accantosystems.stratoss.driver.ucd.model.heat.HeatTemplate;
import com.accantosystems.stratoss.driver.ucd.model.heat.Parameter;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceInstance;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessDetails;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessInfo;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessProperty;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessRequest;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessRequestStatus;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessResponse;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Blueprint;
import com.accantosystems.stratoss.driver.ucd.model.ucd.CloudProject;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Component;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ComponentEnvironmentProperty;
import com.accantosystems.stratoss.driver.ucd.model.ucd.DeployRequest;
import com.accantosystems.stratoss.driver.ucd.model.ucd.DeployResponse;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Environment;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Environment.StackStatus;
import com.accantosystems.stratoss.driver.ucd.model.ucd.EnvironmentProperty;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Flavor;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Resource;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ValidateError;
import com.accantosystems.stratoss.driver.ucd.utils.HeatTemplateUtils;
import com.accantosystems.stratoss.driver.ucd.web.rest.ResourceNotFoundException;
import com.accantosystems.stratoss.driver.ucd.web.rest.ResourceTypeNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UrbanCodeDeployClient {

	private static final Logger logger = LoggerFactory.getLogger( UrbanCodeDeployClient.class );
	
	private final RestTemplate restTemplate;
	
	@Autowired
	public UrbanCodeDeployClient(RestTemplateBuilder restTemplateBuilder, UCDProperties properties) {
    	restTemplateBuilder.additionalInterceptors(new LoggingRequestInterceptor());
		restTemplate = restTemplateBuilder.build();
		// Need to set the request factory on the RestTemplate instance itself rather than via the RestTemplateBuilder
		// See https://github.com/spring-projects/spring-boot/issues/6686
		if (properties.isAllowSelfSignedCertificates()) {
			// NOTE: Setting this option in JUnit tests will cause connect errors as it hides the test RestTemplate
			logger.warn("Allowing self-signed certificates. This is potentially insecure");
	    	restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SelfSignedCertificateClientHttpRequestFactory()));
		} else {
			// Wrap the existing request factory (useful for testing as the @RestClientTest replaces this with MockRestServerService#MockClientHttpRequestFactory
			restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		}
	}

	public List<Blueprint> getBlueprints(final UrbanCodeDeployEnvironment ucdEnvironment) {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", patternsServer.getUrl());
		ResponseEntity<List<Blueprint>> response = restTemplate.exchange("{host}/landscaper/rest/blueprint/", HttpMethod.GET, request, new ParameterizedTypeReference<List<Blueprint>>() {}, urlParameters);
		
		return response.getBody();
	}
	
	public Blueprint getBlueprint(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName) throws ResourceTypeNotFoundException {
		String blueprintLocation = getBlueprintLocation( ucdEnvironment, blueprintName );
		return getBlueprint(ucdEnvironment, blueprintName, blueprintLocation);
	}
	
	private Blueprint getBlueprint(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName, final String blueprintLocation) {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		headers.add("Location", blueprintLocation);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", patternsServer.getUrl());
		urlParameters.put("blueprintName", blueprintName);
		ResponseEntity<Blueprint> response = restTemplate.exchange("{host}/landscaper/rest/blueprint/{blueprintName}", HttpMethod.GET, request, Blueprint.class, urlParameters);
		
		return response.getBody();
	}
	
	public Blueprint findBlueprint(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName) {
		// We need to find the blueprint (which is environment and user specific)
		// There should be a better way, but for now we retrieve all blueprints and try and identify the one needed
		List<Blueprint> blueprints = getBlueprints(ucdEnvironment);
		for (Blueprint blueprint : blueprints) {
			if ( blueprint.getId().equals(blueprintName) ) {
				return blueprint;
			}
		}
		logger.debug("Blueprint [{}] not found in UCDP environment [{}] with username [{}]", blueprintName, ucdEnvironment.getUcdPatterns().getUrl(), ucdEnvironment.getUcdPatterns().getUsername());
		return null;
	}
	
	public String getBlueprintLocation(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName) throws ResourceTypeNotFoundException {
		Blueprint blueprint = findBlueprint(ucdEnvironment, blueprintName);
		if ( blueprint == null ) {
			throw new ResourceTypeNotFoundException(String.format("Cannot find blueprint [%s]", blueprintName));
		}
		return blueprint.getLocation();
	}
	
	public DeployRequest createDeployRequest(final UrbanCodeDeployEnvironment ucdEnvironment, final String environmentName, final String deploymentLocation, final String blueprintName, final String blueprintLocation, final Map<String, String> properties) throws DriverExecutionException, MissingParametersException {
		String cloudProjectId = getCloudProjectId(ucdEnvironment, deploymentLocation);
		
		DeployRequest deployRequest = new DeployRequest();
		deployRequest.setCloudProjectId( cloudProjectId );
		deployRequest.setEnvironmentName( environmentName );
		
		checkRequiredParameters(ucdEnvironment, blueprintName, blueprintLocation, properties, deployRequest);
		
		return deployRequest;
	}
	
	public List<String> validateBlueprint(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName, final String blueprintLocation, final DeployRequest deployRequest) throws DriverExecutionException {
		List<String> validationErrors = new ArrayList<>();
		HttpHeaders headers = createDeployHttpHeaders(ucdEnvironment, blueprintLocation);
		
		deployRequest.setValidate(true);
		HttpEntity<DeployRequest> httpEntity = new HttpEntity<>(deployRequest, headers);
		
		ResponseEntity<String> response;
		try {
			Map<String, String> urlParameters = new HashMap<>();
			urlParameters.put("name", blueprintName);
			urlParameters.put("host", ucdEnvironment.getUcdPatterns().getUrl());
			response = restTemplate.exchange( "{host}/landscaper/rest/blueprint/{name}/validate", HttpMethod.PUT, httpEntity, String.class, urlParameters);
	        if ( !HttpStatus.OK.equals( response.getStatusCode() ) ) {
	        	validationErrors.add("Failed to validate blueprint: " + response.getStatusCode().getReasonPhrase());
	        }
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (HttpStatus.BAD_REQUEST.equals( e.getStatusCode() )) {
				// This error means we sent an invalid request
				// Effectively, we can ignore the exception as we'll send back false from this method anyway
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.findAndRegisterModules();
					List<ValidateError> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<List<ValidateError>>() {});
					for (ValidateError validateError : errors) {
						logger.warn("Validation error received: {}", validateError.toString());
						validationErrors.add(validateError.getErrorMessage());
					}
				} catch (IOException e1) {
					throw new DriverExecutionException("Exception parsing validation errors: " + e.getResponseBodyAsString(), e1);
				}
			} else {
				throw new DriverExecutionException(e);
			}
		}
		
		return validationErrors;
	}
	
	public String deployBlueprint(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName, final String blueprintLocation, final DeployRequest deployRequest) throws DriverExecutionException {
		HttpHeaders headers = createDeployHttpHeaders(ucdEnvironment, blueprintLocation);
		HttpEntity<DeployRequest> httpEntity = new HttpEntity<>(deployRequest, headers);
		
		ResponseEntity<DeployResponse> response;
		try {
			Map<String, String> urlParameters = new HashMap<>();
			urlParameters.put("name", blueprintName);
			urlParameters.put("host", ucdEnvironment.getUcdPatterns().getUrl());
			response = restTemplate.exchange( "{host}/landscaper/rest/blueprint/{name}/deploy", HttpMethod.PUT, httpEntity, DeployResponse.class, urlParameters);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			logger.error("Exception deploying blueprint: " + e.getResponseBodyAsString(), e);
			throw new DriverExecutionException("Failed to deploy blueprint: " + e.getMessage());
		}
		return response.getBody().getId();		
	}

	private HttpHeaders createDeployHttpHeaders(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintLocation) {
		HttpHeaders headers = createDefaultHttpHeaders(ucdEnvironment.getUcdPatterns());
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Location", blueprintLocation);
		// Header "X-Region-Name" can be used to specify cloud region		
		
		return headers;
	}

	/**
	 * @param serviceComponent
	 * @param parameters
	 * @param RPCResponse
	 * @param ucdServer
	 * @param deployRequest
	 * @return
	 * @throws DriverExecutionException 
	 * @throws MissingParametersException 
	 * @throws DescriptorNotFoundException 
	 */
	private void checkRequiredParameters(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName, final String blueprintLocation, final Map<String, String> properties, DeployRequest deployRequest) throws MissingParametersException, DriverExecutionException {
		Blueprint blueprint = getBlueprint(ucdEnvironment, blueprintName, blueprintLocation);
		Map<String, Parameter> blueprintParameters = HeatTemplateUtils.getHeatTemplateFromBlueprint(blueprint).getParameters();
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		StringBuilder missingParameters = new StringBuilder();
		
		if ( blueprintParameters != null ) {
			blueprintParameters.entrySet().forEach( entry -> {
				// Set default value (if present) first, then override with values passed in
				String value = entry.getValue().getDefault();
				if ( properties.containsKey(entry.getKey()) && properties.get(entry.getKey()) != null ) {
					value = properties.get(entry.getKey());
				}
				
				// Override values with special values for UCD environment
				if ( "ucd_password".equals( entry.getKey() )) {
					value = ucdServer.getPassword();
				}
				if ( "ucd_user".equals( entry.getKey() )) {
					value = ucdServer.getUsername();
				}
				// Not overriding ucd_server_url parameter in case the images see a different URL to the Orchestrator
				
				if ( value == null ) {
					// Uh-oh, we missed a parameter!
					missingParameters.append( "Missing parameter [" + entry.getKey() + "]" + System.lineSeparator() );
				} else {
					deployRequest.getParameters().put(entry.getKey(), value);
				}
			});
		}
		
		if ( missingParameters.length() > 0 ) {
			throw new MissingParametersException(missingParameters.toString());
		}
	}
	
	public void deleteEnvironment(final UrbanCodeDeployEnvironment ucdEnvironment, final String environmentId, final String environmentName) throws DriverExecutionException {
		if ( environmentName == null || environmentName.isEmpty() ) {
			throw new DriverExecutionException("Cannot delete an environment without a name");
		}
		if ( environmentId == null || environmentId.isEmpty() ) {
			throw new DriverExecutionException("Cannot delete an environment without an id");
		}
		String[] environmentParts = environmentId.split(Constants.RESOURCE_TYPE_DELIMITER);
		if ( environmentParts == null || environmentParts.length < 2 ) {
			throw new DriverExecutionException(String.format("Cannot find resource instance with incorrect resource type syntax [%s]", environmentId));
		}
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdEnvironment.getUcdPatterns());
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		try {
			Map<String, String> urlParameters = new HashMap<>();
			urlParameters.put( "environmentId", environmentParts[1] );
			urlParameters.put( "environmentName", environmentName );
			urlParameters.put( "host", ucdEnvironment.getUcdPatterns().getUrl() );
			restTemplate.exchange( "{host}/landscaper/rest/environment/{environmentName}/{environmentId}", HttpMethod.DELETE, request, String.class, urlParameters);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			logger.error("Exception deleting environment: " + e.getResponseBodyAsString(), e);
			throw new DriverExecutionException("Failed to delete environment: " + e.getMessage());
		}
		
//		waitForEnvironmentToReachState(ucdEnvironment, environmentId, StackStatus.DELETE_IN_PROGRESS, StackStatus.DELETE_COMPLETE);
//		return new RPCResponse(RPCResponse.ResponseStatus.SUCCESS, false);
	}

	public Environment getEnvironment(final UrbanCodeDeployEnvironment ucdEnvironment, final String environmentId) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);

		String[] environmentParts = environmentId.split(Constants.RESOURCE_TYPE_DELIMITER);
		if ( environmentParts == null || environmentParts.length < 2 ) {
			throw new DriverExecutionException(String.format("Cannot find resource instance with incorrect resource type syntax [%s]", environmentId));
		}
		String id = environmentParts[1];
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "id", id );
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<Environment> response = restTemplate.exchange( "{host}/landscaper/rest/environment/{id}", HttpMethod.GET, request, Environment.class, urlParameters);
		
		return response.getBody();
	}
	
	private List<Environment> getEnvironments(final UrbanCodeDeployEnvironment ucdEnvironment) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<List<Environment>> response = restTemplate.exchange( "{host}/landscaper/rest/environment/", HttpMethod.GET, request, new ParameterizedTypeReference<List<Environment>>() {}, urlParameters);
		
		return response.getBody();
	}
	
	private String getUniqueEnvironmentName(final UrbanCodeDeployEnvironment ucdEnvironment, final ResourceInstance instance) {
//		UrbanCodeDeployServer patternsServer = getUCDEnvironment(ucdEnvironmentName).getUcdPatterns();
//		
//		HttpHeaders headers = getHeaders(patternsServer);
//		HttpEntity<String> request = new HttpEntity<String>(null, headers);
//		
//		Map<String, String> urlParameters = new HashMap<String, String>();
//		urlParameters.put( "name", environmentName );
//		urlParameters.put( "host", patternsServer.getUrl() );
//		ResponseEntity<UniqueEnvironmentNameResponse> response = restTemplate.exchange( "{host}/landscaper/rest/blueprint/{name}/uniqueEnvironmentName", HttpMethod.GET, request, UniqueEnvironmentNameResponse.class, urlParameters);
//		logger.debug("Received response for GET unique environment name: {}", response.getBody());
//		
//		return response.getBody().getUnique();
		
		// FIXME Temporary fix due to UCDP not always being able to return a unique environment name
		StringBuilder newEnvironmentName = new StringBuilder();
		if ( instance.getResourceName().matches("^\\d.*") ) {
			newEnvironmentName.append("X"); // Cannot start environment names with alphanumeric characters
		}
		newEnvironmentName.append( instance.getResourceName() + "-" );
		newEnvironmentName.append( instance.getResourceId().substring(0, 8) ); // Only take the first 8 characters to reduce the overall length (should still be quite random)
		return newEnvironmentName.toString().replace(" ", "_"); // Cannot have spaces in environment names
	}
	
	public String getNetworkId(final UrbanCodeDeployEnvironment ucdEnvironment, final String networkName) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<List<Resource>> response = restTemplate.exchange( "{host}/landscaper/rest/cloud/resources/networks", HttpMethod.GET, request, new ParameterizedTypeReference<List<Resource>>() {}, urlParameters);
		
		for (Resource network : response.getBody()) {
			if ( network.getName().equals(networkName) ) {
				return network.getId();
			}
		}
		
		logger.warn("Couldn't find network id for [{}]", networkName);
		return networkName;
	}
	
	public String getImageId(final UrbanCodeDeployEnvironment ucdEnvironment, final String imageName) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<List<Resource>> response = restTemplate.exchange( "{host}/landscaper/rest/cloud/resources/images", HttpMethod.GET, request, new ParameterizedTypeReference<List<Resource>>() {}, urlParameters);
		
		for (Resource image : response.getBody()) {
			if ( image.getName().equals(imageName) ) {
				return image.getId();
			}
		}
		
		logger.warn("Couldn't find image id for [{}]", imageName);
		return imageName;
	}
	
	public String getKeypairId(final UrbanCodeDeployEnvironment ucdEnvironment, final String keypairName) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<List<Resource>> response = restTemplate.exchange( "{host}/landscaper/rest/cloud/resources/keypairs", HttpMethod.GET, request, new ParameterizedTypeReference<List<Resource>>() {}, urlParameters);
		
		for (Resource keypair : response.getBody()) {
			if ( keypair.getName().equals(keypairName) ) {
				return keypair.getId();
			}
		}
		
		logger.warn("Couldn't find key pair id for [{}]", keypairName);
		return keypairName;
	}
	
	public String getFlavorId(final UrbanCodeDeployEnvironment ucdEnvironment, final String flavorName) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<List<Flavor>> response = restTemplate.exchange( "{host}/landscaper/rest/cloud/resources/flavors", HttpMethod.GET, request, new ParameterizedTypeReference<List<Flavor>>() {}, urlParameters);
		
		for (Flavor flavor : response.getBody()) {
			if ( flavor.getName().equals(flavorName) ) {
				return flavor.getId();
			}
		}
		
		logger.warn("Couldn't find flavor id for [{}]", flavorName);
		return flavorName;
	}
	
	private String getCloudProjectId(final UrbanCodeDeployEnvironment ucdEnvironment, final String cloudProjectName) throws DriverExecutionException {
		UrbanCodeDeployServer patternsServer = ucdEnvironment.getUcdPatterns();
		
		HttpHeaders headers = createDefaultHttpHeaders(patternsServer);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put( "host", patternsServer.getUrl() );
		ResponseEntity<List<CloudProject>> response = restTemplate.exchange( "{host}/landscaper/security/cloudproject/", HttpMethod.GET, request, new ParameterizedTypeReference<List<CloudProject>>() {}, urlParameters);
		
		for (CloudProject cloudProject : response.getBody()) {
			if ( cloudProject.getDisplayName().equals(cloudProjectName) ) {
				return cloudProject.getId();
			}
		}
		
		logger.warn("Couldn't find cloud project id for [{}]", cloudProjectName);
		return cloudProjectName;
	}
	
	/**
	 * @param ucdEnvironmentName
	 * @param environmentId
	 * @return
	 * @throws DriverExecutionException
	 */
	private Environment waitForEnvironmentToReachState(final UrbanCodeDeployEnvironment ucdEnvironment, final String environmentId, final StackStatus waitingState, final StackStatus desiredFinalState) throws DriverExecutionException {
		Environment environment;
		long delay = 0L;
		
		do {
			if ( delay == 0L ) {
				delay = 1000L;
			} else {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					logger.error("Exception caught whilst sleeping", e);
					Thread.currentThread().interrupt();
				}
				if ( delay < 20000L ) {
					delay = Math.round( delay * 1.5 );
				}
			}
			
			environment = getEnvironment(ucdEnvironment, environmentId);
		} while (environment != null && waitingState.toString().equals(environment.getStackStatus()));
		
		if ( environment != null && desiredFinalState.toString().equals(environment.getStackStatus()) ) {
			return environment;
		} else if ( environment != null ){
			throw new DriverExecutionException(String.format("Failed to get environment to desired status [%s] with reason: %s", desiredFinalState, environment.getStackStatusReason()));
		} else {
			throw new DriverExecutionException("Failed to get environment details");
		}
	}

	public String runApplicationProcess(final UrbanCodeDeployEnvironment ucdEnvironment, final String blueprintName, final String environmentName, final String processName, final Map<String, String> parameters, final boolean failIfProcessNotFound) throws DriverExecutionException, ResourceTypeNotFoundException {
		Blueprint blueprint = getBlueprint(ucdEnvironment, blueprintName);
		HeatTemplate heatTemplate = HeatTemplateUtils.getHeatTemplateFromBlueprint(blueprint);
		String applicationName = HeatTemplateUtils.getApplicationNameFromHeatTemplate(heatTemplate);
		String componentName = HeatTemplateUtils.getComponentNameFromHeatTemplate(heatTemplate);
		if ( applicationName != null && !applicationName.isEmpty() && componentName != null && !componentName.isEmpty() ) {
			List<ApplicationProcessInfo> processes = getProcessesForApplication( ucdEnvironment, applicationName );
			if ( processes != null && !processes.isEmpty() ) {
				for (ApplicationProcessInfo applicationProcessInfo : processes) {
					logger.debug("Got application process: {}", applicationProcessInfo);
					if ( processName.equalsIgnoreCase(applicationProcessInfo.getName()) ) {
						return submitApplicationProcessRequest(ucdEnvironment, applicationName, applicationProcessInfo.getName(), componentName, environmentName, parameters);
					}
				}
			}
		}
		if ( failIfProcessNotFound ) {
			throw new DriverExecutionException("Cannot find UCD application process with name " + processName + " in application " + applicationName);
		}
		return null;
	}
	
	private String submitApplicationProcessRequest(final UrbanCodeDeployEnvironment ucdEnvironment, final String applicationName, final String processName, final String componentName, final String environmentName, final Map<String, String> parameters) throws DriverExecutionException {
		HttpHeaders headers = createDefaultHttpHeaders(ucdEnvironment.getUcdServer());
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		final ApplicationProcessRequest applicationProcessRequest = new ApplicationProcessRequest();
		applicationProcessRequest.setApplication(applicationName);
		applicationProcessRequest.setApplicationProcess(processName);
		applicationProcessRequest.setEnvironment(environmentName);
		applicationProcessRequest.setOnlyChanged(false);
		// Get application process properties and check we've filled all required properties
		StringBuilder missingParameters = new StringBuilder();
		List<ApplicationProcessProperty> properties = getApplicationProcessProperties(ucdEnvironment, applicationName, processName);
		for (ApplicationProcessProperty applicationProcessProperty : properties) {
			Object value = parameters.get( applicationProcessProperty.getName() );
			if ( value != null ) {
				applicationProcessRequest.getProperties().put( applicationProcessProperty.getName(), value.toString() );
			} else if ( applicationProcessProperty.getValue() == null) {
				missingParameters.append("Missing parameter [" + applicationProcessProperty.getName() + "]" + System.lineSeparator());
			}
		}
		if ( missingParameters.length() > 0 ) {
			throw new DriverExecutionException(missingParameters.toString());
		}
		// Set versions
		applicationProcessRequest.getVersions().add(applicationProcessRequest.new ComponentVersion(componentName, "latest"));
		
		HttpEntity<ApplicationProcessRequest> request = new HttpEntity<>(applicationProcessRequest, headers);
		
		ResponseEntity<ApplicationProcessResponse> response;
		try {
			Map<String, String> urlParameters = new HashMap<>();
			urlParameters.put("host", ucdEnvironment.getUcdServer().getUrl());
			response = restTemplate.exchange( "{host}/cli/applicationProcessRequest/request", HttpMethod.PUT, request, ApplicationProcessResponse.class, urlParameters);
		} catch (HttpClientErrorException e) {
			if (HttpStatus.BAD_REQUEST.equals( e.getStatusCode() )) {
				// This error means we sent an invalid request
				// Effectively, we can ignore the exception as we'll send back false from this method anyway
				logger.warn("Bad request: {}", e.getResponseBodyAsString());
				throw new DriverExecutionException("Failed to send a correct request to start an application process");
			} else {
				logger.error("Failed to execute an application process", e);
				throw new DriverExecutionException("Failed to execute an application process, error: " + e.getResponseBodyAsString());
			}
		}
		
		return response.getBody().getRequestId();
	}
	
    public ApplicationProcessDetails getApplicationProcessDetails(final UrbanCodeDeployEnvironment ucdEnvironment, final String applicationProcessId, final int applicationProcessVersion) {
        UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
        
        HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("host", ucdServer.getUrl());
        urlParameters.put("id", applicationProcessId);
        urlParameters.put("version", Integer.toString(applicationProcessVersion));
        ResponseEntity<ApplicationProcessDetails> response = restTemplate.exchange( "{host}/rest/deploy/applicationProcess/{id}/{version}", HttpMethod.GET, request, ApplicationProcessDetails.class, urlParameters);
        return response.getBody();
    }
	
	public List<ApplicationProcessInfo> getProcessesForApplication(final UrbanCodeDeployEnvironment ucdEnvironment, final String applicationName) {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("applicationName", applicationName);
		ResponseEntity<List<ApplicationProcessInfo>> response = restTemplate.exchange( "{host}/rest/deploy/application/{applicationName}/processes/false", HttpMethod.GET, request, new ParameterizedTypeReference<List<ApplicationProcessInfo>>() {}, urlParameters);
        
		return response.getBody();
	}
	
    public Component getComponentInfo(final UrbanCodeDeployEnvironment ucdEnvironment, final String componentName) {
        UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
        
        HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("host", ucdServer.getUrl());
        urlParameters.put("componentName", componentName);
        ResponseEntity<Component> response = restTemplate.exchange( "{host}/cli/component/info?component={componentName}", HttpMethod.GET, request, Component.class, urlParameters);
        return response.getBody();
    }
	
	private ApplicationProcessInfo getApplicationProcessInfo(final UrbanCodeDeployEnvironment ucdEnvironment, final String applicationName, final String processName) throws DriverExecutionException {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("application", applicationName);
		urlParameters.put("processName", processName);
		ResponseEntity<ApplicationProcessInfo> response = restTemplate.exchange( "{host}/cli/applicationProcess/info?application={application}&applicationProcess={processName}", HttpMethod.GET, request, ApplicationProcessInfo.class, urlParameters);
        return response.getBody();
	}
	
	private List<ApplicationProcessProperty> getApplicationProcessProperties(final UrbanCodeDeployEnvironment ucdEnvironment, final String applicationName, final String processName) throws DriverExecutionException {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("application", applicationName);
		urlParameters.put("processName", processName);
		// optional parameter 'snapshot'?
		ResponseEntity<List<ApplicationProcessProperty>> response = restTemplate.exchange( "{host}/cli/applicationProcess/unfilledProperties?application={application}&processName={processName}", HttpMethod.GET, request, new ParameterizedTypeReference<List<ApplicationProcessProperty>>() {}, urlParameters);
        return response.getBody();
	}
	
	public ApplicationProcessRequestStatus getApplicationProcessRequestStatus(final UrbanCodeDeployEnvironment ucdEnvironment, final String requestId) throws DriverExecutionException {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("requestId", requestId);
		ResponseEntity<ApplicationProcessRequestStatus> response = restTemplate.exchange( "{host}/cli/applicationProcessRequest/requestStatus?request={requestId}", HttpMethod.GET, request, ApplicationProcessRequestStatus.class, urlParameters);
        return response.getBody();
	}
	
	public List<ComponentEnvironmentProperty> getComponentEnvironmentProperties(final UrbanCodeDeployEnvironment ucdEnvironment, final String componentId, final int componentVersion) {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("componentId", componentId);
		urlParameters.put("componentVersion", Integer.toString(componentVersion));
		ResponseEntity<List<ComponentEnvironmentProperty>> response = restTemplate.exchange( "{host}/property/propSheetDef/components&{componentId}&environmentPropSheetDef.{componentVersion}/propDefs", HttpMethod.GET, request, new ParameterizedTypeReference<List<ComponentEnvironmentProperty>>() {}, urlParameters);
        return response.getBody();
	}
	
	private List<EnvironmentProperty> getEnvironmentProperties(final UrbanCodeDeployEnvironment ucdEnvironment, final String environmentName, final String applicationName) throws DriverExecutionException {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("environment", environmentName);
		urlParameters.put("application", applicationName);
		ResponseEntity<List<EnvironmentProperty>> response = restTemplate.exchange( "{host}/cli/environment/getProperties?environment={environment}&application={application}", HttpMethod.GET, request, new ParameterizedTypeReference<List<EnvironmentProperty>>() {}, urlParameters);
        return response.getBody();
	}
	
	public List<EnvironmentProperty> getComponentEnvironmentProperties(final UrbanCodeDeployEnvironment ucdEnvironment, final String environmentName, final String applicationName, final String componentName) throws DriverExecutionException, ResourceNotFoundException {
		UrbanCodeDeployServer ucdServer = ucdEnvironment.getUcdServer();
		
		HttpHeaders headers = createDefaultHttpHeaders(ucdServer);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(null, headers);
		
		Map<String, String> urlParameters = new HashMap<>();
		urlParameters.put("host", ucdServer.getUrl());
		urlParameters.put("environment", environmentName);
		urlParameters.put("application", applicationName);
		urlParameters.put("component", componentName);
		ResponseEntity<List<EnvironmentProperty>> response;
		try {
			response = restTemplate.exchange( "{host}/cli/environment/componentProperties?environment={environment}&application={application}&component={component}", HttpMethod.GET, request, new ParameterizedTypeReference<List<EnvironmentProperty>>() {}, urlParameters);
		} catch (HttpClientErrorException e) {
			if ( e.getStatusCode() == HttpStatus.NOT_FOUND ) {
				throw new ResourceNotFoundException(e);
			}
			throw e;
		}
        return response.getBody();
	}
	
    /**
     * Add HTTP Authorization header, using Basic-Authentication to send user-credentials.
     * @return
     */
    HttpHeaders createDefaultHttpHeaders(UrbanCodeDeployServer ucdServer){
        String plainCredentials = ucdServer.getUsername() + ":" + ucdServer.getPassword();
        String base64Credentials = Base64.getEncoder().encodeToString(plainCredentials.getBytes());
         
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Credentials);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

}
