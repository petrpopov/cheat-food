package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.FoursquareUser;

import java.io.IOException;

public class FoursquareUserContainerDeserializer extends AbstractFoursquareDeserializer<FoursquareUserContainer> {
    
	@Override
	public FoursquareUserContainer deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return new FoursquareUserContainer(deserializeNestedResponseObject(jp, "user", FoursquareUser.class));
	}
	
	
}
