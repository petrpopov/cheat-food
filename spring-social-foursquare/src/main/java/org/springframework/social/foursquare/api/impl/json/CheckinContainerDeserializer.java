package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Checkin;

import java.io.IOException;

public class CheckinContainerDeserializer extends AbstractFoursquareDeserializer<CheckinContainer> {
	@Override
	public CheckinContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		CheckinContainer result = new CheckinContainer(deserializeNestedResponseObject(jp, "checkin", Checkin.class));
		return result;
	}
}
