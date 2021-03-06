package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Specials;

import java.io.IOException;

public class SpecialsContainerDeserializer extends AbstractFoursquareDeserializer<SpecialsContainer> {

	@Override
	public SpecialsContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		return new SpecialsContainer(deserializeNestedResponseObject(jp, "specials", Specials.class));
	}
}
