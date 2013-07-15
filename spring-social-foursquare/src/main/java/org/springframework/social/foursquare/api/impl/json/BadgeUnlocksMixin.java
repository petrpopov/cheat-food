package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.Checkin;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class BadgeUnlocksMixin {
    @JsonCreator
    BadgeUnlocksMixin(
            @JsonProperty("checkins") List<Checkin> checkins){}

}
