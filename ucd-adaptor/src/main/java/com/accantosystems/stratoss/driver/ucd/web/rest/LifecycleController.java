package com.accantosystems.stratoss.driver.ucd.web.rest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.client.MissingParametersException;
import com.accantosystems.stratoss.driver.ucd.client.UrbanCodeDeployClient;
import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;
import com.accantosystems.stratoss.driver.ucd.config.UCDProperties.UrbanCodeDeployEnvironment;
import com.accantosystems.stratoss.driver.ucd.model.heat.HeatTemplate;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.TransitionRequest;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.TransitionStatus;
import com.accantosystems.stratoss.driver.ucd.model.ucd.ApplicationProcessRequestStatus;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Blueprint;
import com.accantosystems.stratoss.driver.ucd.model.ucd.DeployRequest;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Environment;
import com.accantosystems.stratoss.driver.ucd.model.ucd.Environment.StackStatus;
import com.accantosystems.stratoss.driver.ucd.model.ucd.EnvironmentOutputParameter;
import com.accantosystems.stratoss.driver.ucd.model.ucd.EnvironmentProperty;
import com.accantosystems.stratoss.driver.ucd.service.TransitionStatusService;
import com.accantosystems.stratoss.driver.ucd.utils.HeatTemplateUtils;
import com.accantosystems.stratoss.driver.ucd.utils.UCDUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/resource-manager/lifecycle")
public class LifecycleController {
	
	private static final Logger logger = LoggerFactory.getLogger(LifecycleController.class);

	private final TransitionStatusService transitionStatusService;
	private final UrbanCodeDeployClient ucdClient;
	private final UCDProperties properties;
	
	@Autowired
	public LifecycleController(TransitionStatusService transitionStatusService, UrbanCodeDeployClient ucdClient, UCDProperties properties) {
		super();
		this.transitionStatusService = transitionStatusService;
		this.ucdClient = ucdClient;
		this.properties = properties;
	}
	
	@PostMapping("/transitions")
	@ApiOperation(value="Create Resource Transition", notes="Requests a transition or operation is performed on a resource instance")
	public ResponseEntity<TransitionStatus> createTransition(@RequestBody TransitionRequest transitionRequest) throws DriverExecutionException, ResourceTypeNotFoundException {
		logger.debug(String.format("Received request to perform transition [%s]", transitionRequest));
		TransitionStatus returnStatus = new TransitionStatus();
		returnStatus.setStartedAt(OffsetDateTime.now());
		
		UrbanCodeDeployEnvironment ucdEnvironment = UCDUtils.getUCDEnvironment(properties, transitionRequest.getDeploymentLocation());
		if ( Constants.TRANSITION_NAME_INSTALL.equalsIgnoreCase(transitionRequest.getTransitionName()) ) {			
			String blueprintName = UCDUtils.getBlueprintName(transitionRequest.getResourceType());
			String blueprintLocation;
			try {
				blueprintLocation = ucdClient.getBlueprintLocation(ucdEnvironment, blueprintName);
			} catch (ResourceTypeNotFoundException e) {
				throw new DriverExecutionException(e);
			}
			String randomSuffix = UUID.randomUUID().toString();
			String environmentName = transitionRequest.getResourceName() + "_" + randomSuffix.substring(randomSuffix.length() - 8);
			
			DeployRequest deployRequest;
			try {
				deployRequest = ucdClient.createDeployRequest(ucdEnvironment, environmentName, transitionRequest.getDeploymentLocation(), blueprintName, blueprintLocation, transitionRequest.getProperties());
			} catch (MissingParametersException e) {
				logger.warn("Unable to create valid deploy request for UCD", e);
				returnStatus.setRequestState(TransitionStatus.State.FAILED);
				returnStatus.setRequestStateReason( e.getMessage() );
				returnStatus.setFinishedAt(OffsetDateTime.now());
				return ResponseEntity.badRequest().body(returnStatus);
			}
			
			List<String> validationErrors = ucdClient.validateBlueprint(ucdEnvironment, blueprintName, blueprintLocation, deployRequest);
			if ( validationErrors != null && !validationErrors.isEmpty() ) {
				String message = validationErrors.stream().reduce( (existing, newEntry) -> existing + System.lineSeparator() + newEntry ).orElse(null);
				logger.warn(String.format("Unable to validate UCD deploy request: %s", message));
				returnStatus.setRequestState(TransitionStatus.State.FAILED);
				returnStatus.setRequestStateReason( message );
				returnStatus.setFinishedAt(OffsetDateTime.now());
				return ResponseEntity.badRequest().body(returnStatus);
			}
			
			String environmentId = ucdClient.deployBlueprint(ucdEnvironment, blueprintName, blueprintLocation, deployRequest);
			returnStatus.setRequestId( Constants.ENVIRONMENT_CREATE_PREFIX + environmentId );
			returnStatus.setRequestState(TransitionStatus.State.IN_PROGRESS);
			returnStatus.setResourceId(transitionRequest.getDeploymentLocation() + Constants.RESOURCE_TYPE_DELIMITER + environmentId);
			returnStatus.getContext().put(Constants.PARAM_DEPLOYMENT_LOCATION, transitionRequest.getDeploymentLocation());
			transitionStatusService.addTransitionStatus(returnStatus);
			return ResponseEntity.ok(returnStatus);
			
		} else if ( Constants.TRANSITION_NAME_UNINSTALL.equalsIgnoreCase(transitionRequest.getTransitionName()) ) {
			Environment environment = ucdClient.getEnvironment(ucdEnvironment, transitionRequest.getResourceId());
			ucdClient.deleteEnvironment(ucdEnvironment, transitionRequest.getResourceId(), environment.getName());
			returnStatus.setRequestId( Constants.ENVIRONMENT_DELETE_PREFIX + transitionRequest.getResourceId() );
			returnStatus.setRequestState(TransitionStatus.State.IN_PROGRESS);
			returnStatus.setResourceId(transitionRequest.getResourceId());
			returnStatus.getContext().put(Constants.PARAM_DEPLOYMENT_LOCATION, transitionRequest.getDeploymentLocation());
			transitionStatusService.addTransitionStatus(returnStatus);
			return ResponseEntity.ok(returnStatus);
		} else {
			Environment environment = ucdClient.getEnvironment(ucdEnvironment, transitionRequest.getResourceId());
			String environmentName = environment.getName();
			Blueprint blueprint = ucdClient.getBlueprint(ucdEnvironment, environment.getBlueprintName());
			HeatTemplate heatTemplate = HeatTemplateUtils.getHeatTemplateFromBlueprint(blueprint);
			String applicationName = HeatTemplateUtils.getApplicationNameFromHeatTemplate(heatTemplate);
			String componentName = HeatTemplateUtils.getComponentNameFromHeatTemplate(heatTemplate);
			
			String requestId = ucdClient.runApplicationProcess(ucdEnvironment, blueprint.getName(), environmentName, transitionRequest.getTransitionName(), transitionRequest.getProperties(), !Constants.VALID_TRANSITION_NAMES.contains(transitionRequest.getTransitionName()));
			if ( requestId == null && Constants.VALID_TRANSITION_NAMES.contains(transitionRequest.getTransitionName()) ) {
				returnStatus.setRequestState( TransitionStatus.State.COMPLETED );
				returnStatus.setResourceId( transitionRequest.getResourceId() );
			} else {
				returnStatus.setRequestId( requestId );
				returnStatus.setRequestState( TransitionStatus.State.IN_PROGRESS );
				returnStatus.setResourceId( transitionRequest.getResourceId() );
				returnStatus.getContext().put( Constants.PARAM_DEPLOYMENT_LOCATION, transitionRequest.getDeploymentLocation() );
				returnStatus.getContext().put( Constants.PARAM_TRANSITION_NAME, transitionRequest.getTransitionName() );
				returnStatus.getContext().put( Constants.PARAM_ENVIRONMENT_NAME, environmentName );
				returnStatus.getContext().put( Constants.PARAM_APPLICATION_NAME, applicationName );
				returnStatus.getContext().put( Constants.PARAM_COMPONENT_NAME, componentName );
				transitionStatusService.addTransitionStatus(returnStatus);
			}
			return ResponseEntity.ok(returnStatus);
		}
	}
		
	@GetMapping("/transitions/{id}/status")
	@ApiOperation(value="Get Resource Transition Status", notes="Returns the status of the specified transition or operation")
	public ResponseEntity<TransitionStatus> getTransitionStatus(@ApiParam(value="Unique id for the resource transition", required=true, example="80fc4a66-7e92-41f8-b4bb-7cb98193f5fa") @PathVariable String id) throws DriverExecutionException, ResourceNotFoundException {
		logger.debug(String.format("Received request for transition status [%s]", id));
		TransitionStatus storedStatus = transitionStatusService.getTransitionStatus(id);
		if ( storedStatus == null ) {
			return ResponseEntity.notFound().build();
		}
		UrbanCodeDeployEnvironment ucdEnvironment = UCDUtils.getUCDEnvironment(properties, storedStatus.getContext().get(Constants.PARAM_DEPLOYMENT_LOCATION));
		if ( id.startsWith(Constants.ENVIRONMENT_CREATE_PREFIX) ) {
			Environment environment = ucdClient.getEnvironment(ucdEnvironment, storedStatus.getResourceId());
			if ( StackStatus.CREATE_IN_PROGRESS.toString().equals(environment.getStackStatus()) ) {
				return ResponseEntity.ok(storedStatus);
			} else if ( StackStatus.CREATE_COMPLETE.toString().equals(environment.getStackStatus()) ) {
				for (EnvironmentOutputParameter outputParameter : environment.getOutputs()) {
					storedStatus.getContext().put(outputParameter.getKey(), outputParameter.getValue());
				}
				storedStatus.setRequestState(TransitionStatus.State.COMPLETED);
				storedStatus.setFinishedAt(OffsetDateTime.now());
				transitionStatusService.removeTransitionStatus(id);
				return ResponseEntity.ok(storedStatus);
			} else {
				for (EnvironmentOutputParameter outputParameter : environment.getOutputs()) {
					storedStatus.getContext().put(outputParameter.getKey(), outputParameter.getValue());
				}
				storedStatus.setRequestState(TransitionStatus.State.FAILED);
				storedStatus.setRequestStateReason(environment.getStackStatusReason());
				storedStatus.setFinishedAt(OffsetDateTime.now());
				transitionStatusService.removeTransitionStatus(id);
				return ResponseEntity.ok(storedStatus);
			}			
		} else if ( id.startsWith(Constants.ENVIRONMENT_DELETE_PREFIX) ) {
			Environment environment = ucdClient.getEnvironment(ucdEnvironment, storedStatus.getResourceId());
			if ( StackStatus.DELETE_IN_PROGRESS.toString().equals(environment.getStackStatus()) ) {
				return ResponseEntity.ok(storedStatus);
			} else if ( StackStatus.DELETE_COMPLETE.toString().equals(environment.getStackStatus()) ) {
				storedStatus.setRequestState(TransitionStatus.State.COMPLETED);
				storedStatus.setFinishedAt(OffsetDateTime.now());
				transitionStatusService.removeTransitionStatus(id);
				return ResponseEntity.ok(storedStatus);
			} else {
				storedStatus.setRequestState(TransitionStatus.State.FAILED);
				storedStatus.setRequestStateReason(environment.getStackStatusReason());
				storedStatus.setFinishedAt(OffsetDateTime.now());
				transitionStatusService.removeTransitionStatus(id);
				return ResponseEntity.ok(storedStatus);
			}			
		} else {
			ApplicationProcessRequestStatus requestStatus = ucdClient.getApplicationProcessRequestStatus(ucdEnvironment, id);
			
			if ( requestStatus == null ) {
				throw new DriverExecutionException("Failed to get application process status");
			}
			if ( Constants.VALID_APPLICATION_PROCESS_RUNNING_STATES.contains(requestStatus.getStatus()) ) {
				return ResponseEntity.ok(storedStatus);
			} else if ( ApplicationProcessRequestStatus.Status.CLOSED.equals(requestStatus.getStatus()) && ApplicationProcessRequestStatus.Result.SUCCEEDED.equals(requestStatus.getResult()) ) {
				List<EnvironmentProperty> environmentProperties = ucdClient.getComponentEnvironmentProperties(ucdEnvironment, storedStatus.getContext().get(Constants.PARAM_ENVIRONMENT_NAME), storedStatus.getContext().get(Constants.PARAM_APPLICATION_NAME), storedStatus.getContext().get(Constants.PARAM_COMPONENT_NAME));
				
				for (EnvironmentProperty environmentProperty : environmentProperties) {
					storedStatus.getContext().put(environmentProperty.getName(), environmentProperty.getValue());
				}
				if ( Constants.TRANSITION_NAME_INTEGRITY.equalsIgnoreCase(storedStatus.getContext().get(Constants.PARAM_TRANSITION_NAME)) ) {
					if ( Constants.SUCCESSFUL_INTEGRITY_RESULT_CODE.equalsIgnoreCase(storedStatus.getContext().get(Constants.PARAM_INTEGRITY_LATEST_RESULT)) ) {
						storedStatus.setRequestState(TransitionStatus.State.COMPLETED);
					} else {
						storedStatus.setRequestState(TransitionStatus.State.FAILED);
					}
					storedStatus.setRequestStateReason(storedStatus.getContext().get(Constants.PARAM_INTEGRITY_LATEST_RESULT_REASON));
				} else {
					storedStatus.setRequestState(TransitionStatus.State.COMPLETED);
				}
				storedStatus.setFinishedAt(OffsetDateTime.now());
				transitionStatusService.removeTransitionStatus(id);
				return ResponseEntity.ok(storedStatus);
			} else {
				storedStatus.setRequestState(TransitionStatus.State.FAILED);
				storedStatus.setRequestStateReason("Application process failed with status [" + requestStatus.getStatus() + "] and result [" + requestStatus.getResult() + "]");
				storedStatus.setFinishedAt(OffsetDateTime.now());
				transitionStatusService.removeTransitionStatus(id);
				return ResponseEntity.ok(storedStatus);
			}
		}
	}
	
}
