package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Special;

import java.io.IOException;

public class SpecialContainerDeserializer extends AbstractFoursquareDeserializer<SpecialContainer> {

	@Override
	public SpecialContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		return new SpecialContainer(deserializeNestedResponseObject(jp, "special", Special.class));
	}
	
}
