package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.springframework.social.foursquare.api.Leaderboard;

import java.io.IOException;

public class LeaderboardContainerDeserializer extends AbstractFoursquareDeserializer<LeaderboardContainer> {

    @Override
    public LeaderboardContainer deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return new LeaderboardContainer(deserializeNestedResponseObject(jp, "leaderboard", Leaderboard.class));
    }
    
}
