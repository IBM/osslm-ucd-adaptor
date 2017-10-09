package com.accantosystems.stratoss.driver.ucd.model.heat;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.accantosystems.stratoss.driver.ucd.model.heat.HeatTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class HeatParserTest {

	@Test
	public void testHeatTemplateWithConstraints() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		HeatTemplate heatTemplate = mapper.readValue(this.getClass().getResourceAsStream("/heat_template_with_constraints.yml"), HeatTemplate.class);
		assertThat(heatTemplate).isNotNull();
	}

}
