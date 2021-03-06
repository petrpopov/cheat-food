package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Photo;

import java.io.IOException;

public class PhotoContainerDeserializer extends AbstractFoursquareDeserializer<PhotoContainer> {
	@Override
	public PhotoContainer deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return new PhotoContainer(deserializeNestedResponseObject(jp, "photo", Photo.class));
	}
}
