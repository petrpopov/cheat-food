package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Todo;

import java.io.IOException;

public class TodoContainerDeserializer extends AbstractFoursquareDeserializer<TodoContainer> {
	@Override
	public TodoContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		return new TodoContainer(deserializeNestedResponseObject(jp, "todo", Todo.class));
	}
}