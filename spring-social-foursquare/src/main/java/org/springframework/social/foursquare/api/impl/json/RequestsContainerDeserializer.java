package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.social.foursquare.api.FoursquareUser;

import java.io.IOException;
import java.util.List;

public class RequestsContainerDeserializer extends AbstractFoursquareDeserializer<RequestsContainer> {
    
    @SuppressWarnings("unchecked") 
    @Override
    public RequestsContainer deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return new RequestsContainer((List<FoursquareUser>) deserializeNestedList(jp, "requests", new TypeReference<List<FoursquareUser>>(){}));
    }
    
}
