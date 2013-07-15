package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.CheckinInfo;

import java.io.IOException;

public class CheckinInfoContainerDeserializer extends AbstractFoursquareDeserializer<CheckinInfoContainer> {
	@Override
	public CheckinInfoContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {

        CheckinInfo info = deserializeNestedResponseObject(jp, "checkins", CheckinInfo.class);
        CheckinInfoContainer container = new CheckinInfoContainer(info);
        return container;
	}
}