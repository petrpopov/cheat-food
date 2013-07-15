package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.BadgeGroup;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class BadgeSetsMixin {
    @JsonCreator
    BadgeSetsMixin(@JsonProperty("groups") List<BadgeGroup> groups){}
}
