package com.accantosystems.stratoss.driver.ucd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.util.List;

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
import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.ResourceType;
import com.accantosystems.stratoss.driver.ucd.service.DefinitionService;
import com.accantosystems.stratoss.driver.ucd.utils.FileUtils;
import com.accantosystems.stratoss.test.TestConstants;

@RunWith(SpringRunner.class)
@RestClientTest(value={TypeController.class, UrbanCodeDeployClient.class, DefinitionService.class})
@ActiveProfiles({"test"})
public class TypeControllerTest {

	@Autowired
	private MockRestServiceServer mockServer;
	
	@Autowired
	private TypeController typeController;
	
	@Test
	public void testGetTypes() throws IOException {
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");		
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		
		List<ResourceType> returnedTypes = typeController.getTypes();
		assertThat(returnedTypes).isNotEmpty();
		assertThat(returnedTypes.size()).isEqualTo(7);
		for (ResourceType resourceType : returnedTypes) {
			assertThat(resourceType.getDescriptor()).isNull();
		}
		
		mockServer.verify();
	}

	@Test
	public void testGetTypeNetwork() throws ResourceTypeNotFoundException, UnsupportedResourceTypeException, DriverExecutionException, IOException {
		ResponseEntity<ResourceType> returnedType = typeController.getType(TestConstants.NETWORK_RESOURCE_TYPE);
		
		assertThat(returnedType).isNotNull();
		assertThat(returnedType.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedType.getBody()).isNotNull();
		assertThat(returnedType.getBody().getDescriptor()).isNotEmpty();
	}

	@Test
	public void testGetType() throws ResourceTypeNotFoundException, UnsupportedResourceTypeException, DriverExecutionException, IOException {
		final String getBlueprintsResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprints.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getBlueprintsResponse, MediaType.APPLICATION_JSON));

		final String getNetVideoBlueprintResponse = FileUtils.loadFileIntoString("ucd-responses/GET-blueprint-net_video.json");
		mockServer.expect(requestTo("http://localhost:8480/landscaper/rest/blueprint/net_video")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getNetVideoBlueprintResponse, MediaType.APPLICATION_JSON));

		ResponseEntity<ResourceType> returnedType = typeController.getType(TestConstants.NET_VIDEO_RESOURCE_TYPE);
		assertThat(returnedType).isNotNull();
		assertThat(returnedType.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(returnedType.getBody()).isNotNull();
		assertThat(returnedType.getBody().getDescriptor()).isNotEmpty();
		
		mockServer.verify();
	}

}
