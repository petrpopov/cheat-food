package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.CheckinComment;

import java.io.IOException;

public class CheckinCommentContainerDeserializer extends AbstractFoursquareDeserializer<CheckinCommentContainer> {
	@Override
	public CheckinCommentContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		return new CheckinCommentContainer(deserializeNestedResponseObject(jp, "comment", CheckinComment.class));
	}
}
