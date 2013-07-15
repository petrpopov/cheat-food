package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class BadgeImageMixin {
    @JsonCreator
    BadgeImageMixin(
            @JsonProperty("prefix") String prefix,
            @JsonProperty("name") String name,
            @JsonProperty("sizes") List<Integer> sizes){}
}
