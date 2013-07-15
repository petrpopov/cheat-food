package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Tip;

import java.io.IOException;

public class TipContainerDeserializer extends AbstractFoursquareDeserializer<TipContainer> {
	
	@Override
	public TipContainer deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return new TipContainer(deserializeNestedResponseObject(jp, "tip", Tip.class));
	}
	
}
