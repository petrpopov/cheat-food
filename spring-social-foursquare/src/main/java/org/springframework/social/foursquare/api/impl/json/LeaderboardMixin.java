package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.LeaderboardItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LeaderboardMixin {
    @JsonCreator
    LeaderboardMixin(
            @JsonProperty("count") int count,
            @JsonProperty("items") List<LeaderboardItem> items){}
}
