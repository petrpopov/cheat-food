package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.AllSettings;

import java.io.IOException;

public class AllSettingsContainerDeserializer extends AbstractFoursquareDeserializer<AllSettingsContainer> {
	@Override
	public AllSettingsContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		return new AllSettingsContainer(deserializeNestedResponseObject(jp, "settings", AllSettings.class));
	}
}
