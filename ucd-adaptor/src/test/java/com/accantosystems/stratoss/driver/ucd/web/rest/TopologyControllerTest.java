package com.accantosystems.stratoss.driver.ucd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.DeploymentLocation;
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceInstance;
import com.accantosystems.stratoss.driver.ucd.utils.FileUtils;
import com.accantosystems.stratoss.test.TestConstants;

@RunWith(SpringRunner.class)
@RestClientTest(value={TopologyController.class, UrbanCodeDeployClient.class})
@ActiveProfiles({"test"})
public class TopologyControllerTest {

	@Autowired
	private MockRestServiceServer mockServer;
	
	@Autowired
	private TopologyController topologyController;
	
	@Test
	public void testGetDeploymentLocations() {
		List<DeploymentLocation> deploymentLocations = topologyController.getDeploymentLocations();
		assertThat(deploymentLocations).isNotNull();
		assertThat(deploymentLocations.size()).isEqualTo(1);
		assertThat(deploymentLocations).usingFieldByFieldElementComparator().containsExactly(new DeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME, TestConstants.TEST_DEPLOYMENT_LOCATION_TYPE));
	}

	@Test
	public void testGetDeploymentLocation() {
		ResponseEntity<DeploymentLocation> returnedDeploymentLocation = topologyController.getDeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME);
		assertThat(returnedDeploymentLocation).isNotNull();
		assertThat(returnedDeploymentLocation.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedDeploymentLocation.getBody()).isNotNull();
		assertThat(returnedDeploymentLocation.getBody()).isEqualToComparingFieldByField(new DeploymentLocation(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME, TestConstants.TEST_DEPLOYMENT_LOCATION_TYPE));
	}

	@Test
	public void testGetInstances() throws IOException, ResourceNotFoundException, UnsupportedResourceTypeException, DriverExecutionException {
		final String getNetworksResponse = FileUtils.loadFileIntoString("ucd-responses/GET-cloud-resource-networks.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/cloud/resources/networks")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getNetworksResponse, MediaType.APPLICATION_JSON));

		List<ResourceInstance> returnedResources = topologyController.getInstances(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME, Optional.of(TestConstants.NETWORK_RESOURCE_TYPE), Optional.of("VIDEO"));
		
		assertThat(returnedResources).isNotNull();
		assertThat(returnedResources.size()).isEqualTo(1);
		
		mockServer.verify();
	}

	@Test
	public void testGetInstance() throws IOException, DriverExecutionException, ResourceTypeNotFoundException, ResourceNotFoundException {
		final String getEnvironmentResponse = FileUtils.loadFileIntoString("ucd-responses/GET-environment-response.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/environment/" + TestConstants.TEST_ENVIRONMENT_ID)).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getEnvironmentResponse, MediaType.APPLICATION_JSON));
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		final String getCStreamerBlueprintResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprint-c_streamer.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/c_streamer")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getCStreamerBlueprintResponse, MediaType.APPLICATION_JSON));
		final String getComponentPropertiesResponse = FileUtils.loadFileIntoString("ucd-responses/GET-deployEnvironment-componentProperties.json");
		mockServer.expect(requestTo("http://localhost:8480/cli/environment/componentProperties?environment=Demo-3&application=d_streamer_application&component=d_streamer_component")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getComponentPropertiesResponse, MediaType.APPLICATION_JSON));
		
		ResponseEntity<ResourceInstance> returnedResource = topologyController.getInstance(TestConstants.TEST_DEPLOYMENT_LOCATION_NAME + "::" + TestConstants.TEST_ENVIRONMENT_ID);
		
		assertThat(returnedResource).isNotNull();
		assertThat(returnedResource.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedResource.getBody()).isNotNull();
		
		mockServer.verify();
	}

}
