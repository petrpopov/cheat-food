package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;

import java.io.IOException;

public class BooleanValueContainerDeserializer extends AbstractFoursquareDeserializer<BooleanValueContainer> {
	@Override
	public BooleanValueContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		return new BooleanValueContainer(deserializeNestedResponseObject(jp, "value", Boolean.class));
	}
}
