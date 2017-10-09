package com.accantosystems.stratoss.driver.ucd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import com.accantosystems.stratoss.driver.ucd.client.DriverExecutionException;
import com.accantosystems.stratoss.driver.ucd.client.UrbanCodeDeployClient;
import com.accantosystems.stratoss.driver.ucd.config.Constants;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.TransitionRequest;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.TransitionStatus;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.TransitionStatus.State;
import com.accantosystems.stratoss.driver.ucd.service.TransitionStatusService;
import com.accantosystems.stratoss.driver.ucd.utils.FileUtils;
import com.accantosystems.stratoss.test.TestConstants;

@RunWith(SpringRunner.class)
@RestClientTest(value={LifecycleController.class, UrbanCodeDeployClient.class, TransitionStatusService.class})
@ActiveProfiles({"test"})
public class LifecycleControllerTest {

	@Autowired
	private MockRestServiceServer mockServer;
	
	@Autowired
	private LifecycleController lifecycleController;
	
	@Autowired
	private TransitionStatusService transitionStatusService;
	
	@Test
	public void testCreateTransitionInstall() throws DriverExecutionException, ResourceTypeNotFoundException, IOException {
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		final String getCloudProjectResponse = FileUtils.loadFileIntoString("ucd-responses/GET-cloudprojects.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/security/cloudproject/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCloudProjectResponse, MediaType.APPLICATION_JSON));
		final String getCStreamerBlueprintResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprint-c_streamer.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer/validate")).andExpect(method(HttpMethod.PUT)).andRespond(withStatus(HttpStatus.OK));
		final String getDeployResponse = FileUtils.loadFileIntoString("ucd-responses/PUT-deploy-c_streamer-response.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer/deploy")).andExpect(method(HttpMethod.PUT)).andRespond(withSuccess(getDeployResponse, MediaType.APPLICATION_JSON));

		final TransitionRequest transitionRequest = new TransitionRequest();
		transitionRequest.setDeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME);
		transitionRequest.setResourceName(TestConstants.TEST_RESOURCE_NAME);
		transitionRequest.setResourceType(TestConstants.C_STREAMER_RESOURCE_TYPE);
		transitionRequest.setTransitionName("Install");
		transitionRequest.getProperties().put("key_name", "TEST");
		transitionRequest.getProperties().put("referenced-video-network", "TEST");
		ResponseEntity<TransitionStatus> returnedStatus = lifecycleController.createTransition(transitionRequest );
		
		assertThat(returnedStatus).isNotNull();
		assertThat(returnedStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedStatus.getBody()).isNotNull();
		assertThat(returnedStatus.getBody().getRequestId()).isNotEmpty();
		assertThat(returnedStatus.getBody().getResourceId()).isEqualTo(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID);
		assertThat(returnedStatus.getBody().getRequestState()).isEqualTo(State.IN_PROGRESS);
		assertThat(returnedStatus.getBody().getRequestStateReason()).isNull();
		
		mockServer.verify();
	}

	@Test
	public void testCreateTransitionConfigure() throws DriverExecutionException, ResourceTypeNotFoundException, IOException {
		final String getEnvironmentResponse = FileUtils.loadFileIntoString("ucd-responses/GET-environment-response.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/environment/" + TestConstants.TEST_ENVIRONMENT_ID)).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getEnvironmentResponse, MediaType.APPLICATION_JSON));
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		final String getCStreamerBlueprintResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprint-c_streamer.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		final String getApplicationProcessesResponse = FileUtils.loadFileIntoString("ucd-responses/GET-applicationProcesses-c_storage.json");
		mockServer.expect(requestTo("http://localhost:8480/rest/deploy/application/d_streamer_application/processes/false")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getApplicationProcessesResponse, MediaType.APPLICATION_JSON));

		final TransitionRequest transitionRequest = new TransitionRequest();
		transitionRequest.setDeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME);
		transitionRequest.setResourceId(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID);
		transitionRequest.setResourceName(TestConstants.TEST_RESOURCE_NAME);
		transitionRequest.setResourceType(TestConstants.C_STREAMER_RESOURCE_TYPE);
		transitionRequest.setTransitionName("Configure");
		ResponseEntity<TransitionStatus> returnedStatus = lifecycleController.createTransition(transitionRequest );
		
		assertThat(returnedStatus).isNotNull();
		assertThat(returnedStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedStatus.getBody()).isNotNull();
		assertThat(returnedStatus.getBody().getRequestId()).isNull();
		assertThat(returnedStatus.getBody().getResourceId()).isEqualTo(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID);
		assertThat(returnedStatus.getBody().getRequestState()).isEqualTo(State.COMPLETED);
		assertThat(returnedStatus.getBody().getRequestStateReason()).isNull();
		
		mockServer.verify();
	}

	@Test
	public void testCreateTransitionMountStorage() throws DriverExecutionException, ResourceTypeNotFoundException, IOException {
		final String getEnvironmentResponse = FileUtils.loadFileIntoString("ucd-responses/GET-environment-response.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/environment/" + TestConstants.TEST_ENVIRONMENT_ID)).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getEnvironmentResponse, MediaType.APPLICATION_JSON));
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		final String getCStreamerBlueprintResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprint-c_streamer.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		final String getApplicationProcessesResponse = FileUtils.loadFileIntoString("ucd-responses/GET-applicationProcesses-c_storage.json");
		mockServer.expect(requestTo("http://localhost:8480/rest/deploy/application/d_streamer_application/processes/false")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getApplicationProcessesResponse, MediaType.APPLICATION_JSON));
		final String getUnfilledPropertiesResponse = FileUtils.loadFileIntoString("ucd-responses/GET-application-unfilledProperties.json");
		mockServer.expect(requestTo("http://localhost:8480/cli/applicationProcess/unfilledProperties?application=d_streamer_application&processName=MountStorage")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getUnfilledPropertiesResponse, MediaType.APPLICATION_JSON));
		final String putApplicationProcessRequestResponse = FileUtils.loadFileIntoString("ucd-responses/PUT-applicationProcessRequest-response.json");
		mockServer.expect(requestTo("http://localhost:8480/cli/applicationProcessRequest/request")).andExpect(method(HttpMethod.PUT)).andRespond(withSuccess(putApplicationProcessRequestResponse, MediaType.APPLICATION_JSON));

		final TransitionRequest transitionRequest = new TransitionRequest();
		transitionRequest.setDeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME);
		transitionRequest.setResourceId(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID);
		transitionRequest.setResourceName(TestConstants.TEST_RESOURCE_NAME);
		transitionRequest.setResourceType(TestConstants.C_STREAMER_RESOURCE_TYPE);
		transitionRequest.setTransitionName("MountStorage");
		ResponseEntity<TransitionStatus> returnedStatus = lifecycleController.createTransition(transitionRequest );
		
		assertThat(returnedStatus).isNotNull();
		assertThat(returnedStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedStatus.getBody()).isNotNull();
		assertThat(returnedStatus.getBody().getRequestId()).isNotNull();
		assertThat(returnedStatus.getBody().getResourceId()).isEqualTo(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID);
		assertThat(returnedStatus.getBody().getRequestState()).isEqualTo(State.IN_PROGRESS);
		assertThat(returnedStatus.getBody().getRequestStateReason()).isNull();
		
		mockServer.verify();
	}

	@Test
	public void testCreateTransitionMissingRequiredParameter() throws DriverExecutionException, ResourceTypeNotFoundException, IOException {
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		final String getCloudProjectResponse = FileUtils.loadFileIntoString("ucd-responses/GET-cloudprojects.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/security/cloudproject/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCloudProjectResponse, MediaType.APPLICATION_JSON));
		final String getCStreamerBlueprintResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprint-c_streamer.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		
		final TransitionRequest transitionRequest = new TransitionRequest();
		transitionRequest.setDeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME);
		transitionRequest.setResourceName(TestConstants.TEST_RESOURCE_NAME);
		transitionRequest.setResourceType(TestConstants.C_STREAMER_RESOURCE_TYPE);
		transitionRequest.setTransitionName("Install");
		ResponseEntity<TransitionStatus> returnedStatus = lifecycleController.createTransition(transitionRequest );
		
		assertThat(returnedStatus).isNotNull();
		assertThat(returnedStatus.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(returnedStatus.getBody()).isNotNull();
		assertThat(returnedStatus.getBody().getRequestState()).isEqualTo(State.FAILED);
		assertThat(returnedStatus.getBody().getRequestStateReason()).contains("Missing parameter");
		
		mockServer.verify();
	}

	@Test
	public void testGetTransitionStatusInstall() throws IOException, DriverExecutionException, ResourceNotFoundException {
		final String getEnvironmentResponse = FileUtils.loadFileIntoString("ucd-responses/GET-environment-response.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/environment/" + TestConstants.TEST_ENVIRONMENT_ID)).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getEnvironmentResponse, MediaType.APPLICATION_JSON));
		
		ResponseEntity<TransitionStatus> returnedStatus = lifecycleController.getTransitionStatus("envC-" + TestConstants.TEST_ENVIRONMENT_ID);
		
		assertThat(returnedStatus).isNotNull();
		assertThat(returnedStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedStatus.getBody()).isNotNull();
		
		mockServer.verify();
	}

	@Test
	public void testGetTransitionStatusConfigure() throws IOException, DriverExecutionException, ResourceNotFoundException {
		TransitionStatus status = new TransitionStatus();
		status.setRequestId( TestConstants.TEST_APPLICATION_PROCESS_ID );
		status.setRequestState( TransitionStatus.State.IN_PROGRESS );
		status.setResourceId( TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID );
		status.getContext().put( Constants.PARAM_DEPLOYMENT_LOCATION, TestConstants.TEST_DEPLOYMENT_LOCATION_NAME );
		status.getContext().put( Constants.PARAM_TRANSITION_NAME, "Configure" );
		transitionStatusService.addTransitionStatus(status);

		final String getRequestStatusResponse = FileUtils.loadFileIntoString("ucd-responses/GET-environment-response.json");
		mockServer.expect(requestTo("http://localhost:8480/cli/applicationProcessRequest/requestStatus?request=" + TestConstants.TEST_APPLICATION_PROCESS_ID)).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getRequestStatusResponse, MediaType.APPLICATION_JSON));
		
		ResponseEntity<TransitionStatus> returnedStatus = lifecycleController.getTransitionStatus(TestConstants.TEST_APPLICATION_PROCESS_ID);
		
		assertThat(returnedStatus).isNotNull();
		assertThat(returnedStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedStatus.getBody()).isNotNull();
		
		mockServer.verify();
	}

}
