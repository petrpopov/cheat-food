package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.ExploreResponse;

import java.io.IOException;

public class ExploreResponseContainerDeserializer extends AbstractFoursquareDeserializer<ExploreResponseContainer> {
    @Override 
    public ExploreResponseContainer deserialize(JsonParser jp, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        return deserializeResponseObject(jp, ExploreResponseContainer.class, ExploreResponse.class);
    }
}
